package org.csuc.service.analyse;

import com.auth0.jwk.JwkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.csuc.client.Client;
import org.csuc.dao.AnalyseDAO;
import org.csuc.dao.impl.AnalyseDAOImpl;
import org.csuc.entities.Analyse;
import org.csuc.utils.authorization.Authoritzation;
import org.csuc.utils.parser.ParserType;
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
public class Type {

    private static Logger logger = LogManager.getLogger(Type.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    private Client client;

    @GET
    @Path("/user/{user}/type/{type}")
    @Produces(APPLICATION_JSON + "; charset=utf-8")
    public Response getParserByType(@PathParam("user") String user,
                                    @PathParam("type") String type,
                                    @DefaultValue("50") @QueryParam("pagesize") int pagesize,
                                    @DefaultValue("0") @QueryParam("page") int page,
                                    @HeaderParam("Authorization") String authorization) {

        if (Objects.isNull(user)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("user is mandatory")
                            .build()
            );
        }

        if (Objects.isNull(type)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("type is mandatory")
                            .build()
            );
        }

        ParserType parserType = ParserType.convert(type);

        if (Objects.isNull(parserType)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("invalid type")
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
            AnalyseDAO analyseDAO = new AnalyseDAOImpl(Analyse.class, client.getDatastore());
            List<Analyse> queryResults = analyseDAO.getByType(parserType, user, page, pagesize);

            double count = new Long(analyseDAO.countByType(parserType, user)).doubleValue();

            ResponseEchoes response =
                    new ResponseEchoes(type, (int) count, (int) Math.ceil(count / new Long(pagesize).doubleValue()), queryResults.size(), queryResults);

            logger.debug(response);

            return Response.status(Response.Status.ACCEPTED).entity(response).build();
        } catch (Exception e) {
            logger.error(e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
