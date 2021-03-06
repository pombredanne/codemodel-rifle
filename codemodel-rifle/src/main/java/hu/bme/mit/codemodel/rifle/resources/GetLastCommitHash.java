package hu.bme.mit.codemodel.rifle.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import hu.bme.mit.codemodel.rifle.database.DbServices;
import hu.bme.mit.codemodel.rifle.database.DbServicesManager;
import hu.bme.mit.codemodel.rifle.database.ResourceReader;

@Path("lastcommit")
public class GetLastCommitHash {
    private static final String GET_LAST_COMMIT_HASH = ResourceReader.query("getlastcommithash");

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response run(
            @DefaultValue("master")
            @QueryParam("branchid") String branchid
    ) {
        final DbServices dbServices = DbServicesManager.getDbServices(branchid);
        try (Transaction tx = dbServices.beginTx()) {
            final StatementResult result = dbServices.execute(GET_LAST_COMMIT_HASH);

            JSONObject response = new JSONObject();
            while (result.hasNext()) {
                Record next = result.next();

                Object commitHash = next.get("commitHash");
                response.put("commitHash", commitHash);
            }

            return Response.ok(response.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.serverError().build();
    }
}
