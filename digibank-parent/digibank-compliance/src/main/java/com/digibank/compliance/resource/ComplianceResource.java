package com.digibank.compliance.resource;

import com.digibank.compliance.dto.ComplianceRequest;
import com.digibank.compliance.dto.ComplianceResponse;
import com.digibank.compliance.service.ComplianceService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/compliance")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ComplianceResource {

    @Inject
    private ComplianceService complianceService;

    @POST
    public Response createComplianceCheck(@Valid @NotNull ComplianceRequest request) {
        ComplianceResponse created = complianceService.createComplianceCheck(request);
        return Response.created(URI.create("/api/compliance/" + created.getId()))
                .entity(created)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getComplianceCheckById(@PathParam("id") Long id) {
        return complianceService.getComplianceCheckById(id)
                .map(check -> Response.ok(check).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    public Response getAllComplianceChecks() {
        return Response.ok(complianceService.getAllComplianceChecks()).build();
    }

    @GET
    @Path("/by-customer/{customerId}")
    public Response getComplianceChecksByCustomerId(@PathParam("customerId") Long customerId) {
        return Response.ok(complianceService.getComplianceChecksByCustomerId(customerId)).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateComplianceCheck(@PathParam("id") Long id, @Valid @NotNull ComplianceRequest request) {
        ComplianceResponse updated = complianceService.updateComplianceCheck(id, request);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteComplianceCheck(@PathParam("id") Long id) {
        complianceService.deleteComplianceCheck(id);
        return Response.noContent().build();
    }
}
