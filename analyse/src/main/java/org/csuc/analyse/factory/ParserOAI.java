package org.csuc.analyse.factory;

import org.apache.hadoop.fs.FileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.csuc.analyse.strategy.ParserMethod;
import org.csuc.deserialize.JaxbUnmarshal;
import org.openarchives.oai._2.OAIPMHtype;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author amartinez
 */
public class ParserOAI implements Parser {

    private static Logger logger = LogManager.getLogger(ParserOAI.class);

    private ParserMethod method;

    private AtomicInteger iter = new AtomicInteger(0);

    public ParserOAI(ParserMethod method){
        logger.debug(String.format("analyse: %s", getClass().getSimpleName()));
        this.method = method;
    }

    @Override
    public void execute(String fileOrPath) throws Exception {
        throw new IllegalArgumentException("execute fileOrPath is not valid for ParserFile!");
    }

    @Override
    public void execute(URL url)  throws Exception {
        OAIPMHtype OAIPMHtype =
                (OAIPMHtype) new JaxbUnmarshal(url, new Class[]{OAIPMHtype.class}).getObject();

        method.parser(url);
        if (OAIPMHtype.getListRecords().getResumptionToken() != null) {
            if (!OAIPMHtype.getListRecords().getResumptionToken().getValue().isEmpty()) {
                logger.info(iter.incrementAndGet() + "\t" + OAIPMHtype.getListRecords().getResumptionToken().getValue());
                execute(next(url, OAIPMHtype.getListRecords().getResumptionToken().getValue()));
            }
        }
    }

    @Override
    public void XML(OutputStream outs) {
        method.createXML(outs);
    }

    @Override
    public void HDFS_XML(FileSystem fileSystem, org.apache.hadoop.fs.Path dest) throws IOException{
        method.createHDFS_XML(fileSystem, dest);
    }

    @Override
    public void JSON(OutputStream outs) {
        method.createJSON(outs);
    }

    @Override
    public void HDFS_JSON(FileSystem fileSystem, org.apache.hadoop.fs.Path dest) throws IOException {
        method.createHDFS_JSON(fileSystem, dest);
    }


    private URL next(URL url, String resumptionToken) throws MalformedURLException {
        return new URL(String.format("%s?verb=ListRecords&resumptionToken=%s",
                url.toString().replaceAll("\\?verb=.+", ""), resumptionToken));
    }
}
