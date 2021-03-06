package org.csuc.service.analyse;

import com.auth0.jwk.JwkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.csuc.client.Client;
import org.csuc.dao.AnalyseDAO;
import org.csuc.dao.impl.AnalyseDAOImpl;
import org.csuc.utils.authorization.Authoritzation;
import org.csuc.utils.parser.ParserMethod;
import org.csuc.utils.response.ResponseEchoes;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Objects;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * @author amartinez
 */
@Path("/analyse")
public class Method {

    private static Logger logger = LogManager.getLogger(Analyse.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    private Client client;

    @GET
    @Path("/user/{user}/method/{method}")
    @Produces(APPLICATION_JSON + "; charset=utf-8")
    public Response getParserByMethod(@PathParam("user") String user,
                                      @PathParam("method") String method,
                                      @DefaultValue("50") @QueryParam("pagesize") int pagesize,
                                      @DefaultValue("0") @QueryParam("page") int page,
                                      @HeaderParam("Authorization") String authorization) {
        if (Objects.isNull(method)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("method is mandatory")
                            .build()
            );
        }

        if (Objects.isNull(user)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("user is mandatory")
                            .build()
            );
        }

        ParserMethod parserMethod = ParserMethod.convert(method);

        if (Objects.isNull(parserMethod)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("invalid method")
                            .build()
            );
        }

        Authoritzation authoritzation = new Authoritzation(user, authorization.split("\\s")[1]);
        try {
            authoritzation.execute();
        } catch (JwkException e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            AnalyseDAO analyseDAO = new AnalyseDAOImpl(org.csuc.entities.Analyse.class, client.getDatastore());
            List<org.csuc.entities.Analyse> queryResults = analyseDAO.getByMethod(parserMethod, user, page, pagesize);

            double count = new Long(analyseDAO.countByMethod(parserMethod, user)).doubleValue();

            ResponseEchoes response =
                    new ResponseEchoes(method, (int) count, (int) Math.ceil(count / new Long(pagesize).doubleValue()), queryResults.size(), queryResults);

            logger.debug(response);

            return Response.status(Response.Status.ACCEPTED).entity(response).build();
        } catch (Exception e) {
            logger.error(e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

}
