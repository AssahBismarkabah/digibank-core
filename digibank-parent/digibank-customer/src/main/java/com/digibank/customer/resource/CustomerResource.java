package com.digibank.customer.resource;

import com.digibank.customer.dto.CustomerRequest;
import com.digibank.customer.dto.CustomerResponse;
import com.digibank.customer.service.CustomerService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/customers")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    private CustomerService customerService;

    @POST
    public Response createCustomer(@Valid @NotNull CustomerRequest request) {
        CustomerResponse created = customerService.createCustomer(request);
        return Response.created(URI.create("/api/customers/" + created.getId()))
                .entity(created)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") Long id) {
        return customerService.getCustomerById(id)
                .map(customer -> Response.ok(customer).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    public Response getAllCustomers() {
        return Response.ok(customerService.getAllCustomers()).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, @Valid @NotNull CustomerRequest request) {
        CustomerResponse updated = customerService.updateCustomer(id, request);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        customerService.deleteCustomer(id);
        return Response.noContent().build();
    }
}
