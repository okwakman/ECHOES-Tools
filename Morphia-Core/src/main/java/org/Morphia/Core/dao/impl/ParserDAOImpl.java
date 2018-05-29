package org.Morphia.Core.dao.impl;

import com.mongodb.AggregationOptions;
import com.mongodb.WriteResult;
import org.Morphia.Core.dao.ParserDAO;
import org.Morphia.Core.entities.Parser;
import org.Morphia.Core.utils.Status;
import org.Morphia.Core.utils.Aggregation;
import org.Morphia.Core.utils.parser.ParserFormat;
import org.Morphia.Core.utils.parser.ParserMethod;
import org.Morphia.Core.utils.parser.ParserType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static org.mongodb.morphia.aggregation.Group.grouping;


/**
 * @author amartinez
 */
public class ParserDAOImpl extends BasicDAO<Parser, ObjectId> implements ParserDAO {


    private static Logger logger = LogManager.getLogger(ParserDAOImpl.class);

    public ParserDAOImpl(Class<Parser> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    @Override
    public Parser getById(String objectId) throws Exception {
        Parser parser = createQuery().field("_id").equal(objectId).get();
        if(Objects.isNull(parser))  throw new Exception();

        return parser;
    }

    @Override
    public QueryResults<Parser> getByUser(String user, String orderby) throws Exception {
        if(Objects.isNull(user))  throw new Exception();

        return (Objects.nonNull(orderby))
                ? find(createQuery().field("user").equal(user).order(orderby))
                : find(createQuery().field("user").equal(user));
    }

    @Override
    public List<Parser> getByUser(String user, int offset, int limit, String orderby) throws Exception {
        if(Objects.isNull(user))  throw new Exception();


        return (Objects.nonNull(orderby))
                ? find(createQuery().field("user").equal(user).order(orderby)).asList(new FindOptions().skip(offset > 0 ? ( ( offset - 1 ) * limit ) : 0 ).limit(limit))
                : find(createQuery().field("user").equal(user)).asList(new FindOptions().skip(offset > 0 ? ( ( offset - 1 ) * limit ) : 0 ).limit(limit));

    }

    @Override
    public long countByUser(String user) throws Exception {
        if(Objects.isNull(user))  throw new Exception();
        return find(createQuery().field("user").equal(user)).count();
    }

    @Override
    public QueryResults<Parser> getByMethod(ParserMethod method) throws Exception {
        if(Objects.isNull(method))  throw new Exception();

        return find(createQuery().field("method").equal(method));
    }

    @Override
    public QueryResults<Parser> getByMethod(ParserMethod method, String user) throws Exception {
        if(Objects.isNull(method) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("method").equal(method),
                query.criteria("user").equal(user)
        );

        return find(query);
    }

    @Override
    public List<Parser> getByMethod(ParserMethod method, int offset, int limit) throws Exception {
        if(Objects.isNull(method))  throw new Exception();
        //new FindOptions().skip(offset).limit(limit)
        return find(createQuery().field("method").equal(method)).asList(new FindOptions().skip(offset > 0 ? ( ( offset - 1 ) * limit ) : 0 ).limit(limit));
    }

    @Override
    public List<Parser> getByMethod(ParserMethod method, String user, int offset, int limit) throws Exception {
        if(Objects.isNull(method) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("method").equal(method),
                query.criteria("user").equal(user)
        );

        //new FindOptions().skip(offset).limit(limit)
        return find(query).asList(new FindOptions().skip(offset > 0 ? ( ( offset - 1 ) * limit ) : 0 ).limit(limit));
    }

    @Override
    public long countByMethod(ParserMethod method) throws Exception {
        if(Objects.isNull(method))  throw new Exception();
        return find(createQuery().field("method").equal(method)).count();
    }

    @Override
    public long countByMethod(ParserMethod method, String user) throws Exception {
        if(Objects.isNull(method) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("method").equal(method),
                query.criteria("user").equal(user)
        );

        return find(query).count();
    }

    @Override
    public QueryResults<Parser> getByType(ParserType type) throws Exception {
        if(Objects.isNull(type))  throw new Exception();

        return find(createQuery().field("type").equal(type));
    }

    @Override
    public QueryResults<Parser> getByType(ParserType type, String user) throws Exception {
        if(Objects.isNull(type) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("type").equal(type),
                query.criteria("user").equal(user)
        );

        return find(query);
    }

    @Override
    public List<Parser> getByType(ParserType type, int offset, int limit) throws Exception {
        if(Objects.isNull(type))  throw new Exception();

        //new FindOptions().skip(offset).limit(limit)
        return find(createQuery().field("type").equal(type)).asList(new FindOptions().skip(offset > 0 ? ( ( offset - 1 ) * limit ) : 0 ).limit(limit));
    }

    @Override
    public List<Parser> getByType(ParserType type, String user, int offset, int limit) throws Exception {
        if(Objects.isNull(type) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("type").equal(type),
                query.criteria("user").equal(user)
        );

        //new FindOptions().skip(offset).limit(limit)
        return find(query).asList(new FindOptions().skip(offset > 0 ? ( ( offset - 1 ) * limit ) : 0 ).limit(limit));
    }

    @Override
    public long countByType(ParserType type) throws Exception {
        if(Objects.isNull(type))  throw new Exception();
        return find(createQuery().field("type").equal(type)).count();
    }

    @Override
    public long countByType(ParserType type, String user) throws Exception {
        if(Objects.isNull(type) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("type").equal(type),
                query.criteria("user").equal(user)
        );

        return find(query).count();
    }

    @Override
    public QueryResults<Parser> getByFormat(ParserFormat format) throws Exception {
        if(Objects.isNull(format))  throw new Exception();

        return find(createQuery().field("format").equal(format));
    }

    @Override
    public QueryResults<Parser> getByFormat(ParserFormat format, String user) throws Exception {
        if(Objects.isNull(format) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("format").equal(format),
                query.criteria("user").equal(user)
        );

        return find(query);
    }

    @Override
    public List<Parser> getByFormat(ParserFormat format, int offset, int limit) throws Exception {
        if(Objects.isNull(format))  throw new Exception();

        //new FindOptions().skip(offset).limit(limit)
        return find(createQuery().field("format").equal(format)).asList(new FindOptions().skip(offset > 0 ? ( ( offset - 1 ) * limit ) : 0 ).limit(limit));
    }

    @Override
    public List<Parser> getByFormat(ParserFormat format, String user, int offset, int limit) throws Exception {
        if(Objects.isNull(format) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("format").equal(format),
                query.criteria("user").equal(user)
        );

        return find(query).asList();
    }

    @Override
    public long countByFormat(ParserFormat format) throws Exception {
        if(Objects.isNull(format))  throw new Exception();
        return find(createQuery().field("format").equal(format)).count();
    }

    @Override
    public long countByFormat(ParserFormat format, String user) throws Exception {
        if(Objects.isNull(format) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("format").equal(format),
                query.criteria("user").equal(user)
        );

        return find(query).count();
    }

    @Override
    public QueryResults<Parser> getByStatus(Status status) throws Exception {
        if(Objects.isNull(status))  throw new Exception();

        return find(createQuery().field("status").equal(status));
    }

    @Override
    public QueryResults<Parser> getByStatus(Status status, String user) throws Exception {
        if(Objects.isNull(status) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("status").equal(status),
                query.criteria("user").equal(user)
        );

        return find(query);
    }

    @Override
    public List<Parser> getByStatus(Status status, int offset, int limit) throws Exception {
        if(Objects.isNull(status))  throw new Exception();

        //new FindOptions().skip(offset).limit(limit)
        return find(createQuery().field("status").equal(status)).asList(new FindOptions().skip(offset > 0 ? ( ( offset - 1 ) * limit ) : 0 ).limit(limit));
    }

    @Override
    public List<Parser> getByStatus(Status status, String user, int offset, int limit) throws Exception {
        if(Objects.isNull(status) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("status").equal(status),
                query.criteria("user").equal(user)
        );

        //new FindOptions().skip(offset).limit(limit)
        return find(query).asList(new FindOptions().skip(offset > 0 ? ( ( offset - 1 ) * limit ) : 0 ).limit(limit));
    }

    @Override
    public long countByStatus(Status status) throws Exception {
        if(Objects.isNull(status))  throw new Exception();
        return find(createQuery().field("status").equal(status)).count();
    }

    @Override
    public long countByStatus(Status status, String user) throws Exception {
        if(Objects.isNull(status) || Objects.isNull(user))  throw new Exception();

        Query<Parser> query = createQuery();

        query.and(
                query.criteria("status").equal(status),
                query.criteria("user").equal(user)
        );

        return find(query).count();
    }

    @Override
    public Iterator<Aggregation> getStatusAggregation() {
        AggregationOptions options = AggregationOptions.builder().build();

        Iterator<Aggregation> aggregate = getDatastore().createAggregation(Parser.class)
                .group("status",
                        grouping("total", Accumulator.accumulator("$sum", 1))
                )
                .aggregate(Aggregation.class, AggregationOptions.builder().allowDiskUse(true).batchSize(50).build());
        return aggregate;
    }

    @Override
    public Iterator<Aggregation> getStatusAggregation(String user) {
        AggregationOptions options = AggregationOptions.builder().build();

        Iterator<Aggregation> aggregate = getDatastore().createAggregation(Parser.class)
                .match(createQuery().field("user").equal(user))
                .group("status",
                        grouping("total", Accumulator.accumulator("$sum", 1))
                )
                .aggregate(Aggregation.class, AggregationOptions.builder().allowDiskUse(true).batchSize(50).build());

        return aggregate;
    }

    @Override
    public Key<Parser> insert(Parser parser) throws Exception {
        if(Objects.isNull(parser))  throw new Exception();
        Key<Parser> result = save(parser);
        if(Objects.isNull(result))    throw new Exception();
        return result;
    }

    @Override
    public WriteResult deleteById(String objectId) throws Exception {
        if(Objects.isNull(objectId))    throw new Exception();
        WriteResult writeResult = delete(getById(objectId));
        if(Objects.isNull(writeResult))    throw new Exception();
        return writeResult;
    }

    @Override
    public WriteResult deleteByUser(String user) throws Exception {
        if(Objects.isNull(user))    throw new Exception();
        WriteResult writeResult = getDatastore().delete(createQuery().field("user").equal(user));
        if(Objects.isNull(writeResult))    throw new Exception();
        return writeResult;
    }

    @Override
    public WriteResult deleteByMethod(ParserMethod method) throws Exception {
        if(Objects.isNull(method))    throw new Exception();
        WriteResult writeResult = getDatastore().delete(createQuery().field("method").equal(method));
        if(Objects.isNull(writeResult))    throw new Exception();
        return writeResult;
    }

    @Override
    public WriteResult deleteByType(ParserType type) throws Exception {
        if(Objects.isNull(type))    throw new Exception();
        WriteResult writeResult = getDatastore().delete(createQuery().field("type").equal(type));
        if(Objects.isNull(writeResult))    throw new Exception();
        return writeResult;
    }

    @Override
    public WriteResult deleteByStatus(Status status) throws Exception {
        if(Objects.isNull(status))    throw new Exception();
        WriteResult writeResult = getDatastore().delete(createQuery().field("status").equal(status));
        if(Objects.isNull(writeResult))    throw new Exception();
        return writeResult;
    }
}
