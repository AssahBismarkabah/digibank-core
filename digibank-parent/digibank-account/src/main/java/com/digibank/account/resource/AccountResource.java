package com.digibank.account.resource;

import com.digibank.account.dto.AccountRequest;
import com.digibank.account.dto.AccountResponse;
import com.digibank.account.service.AccountService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/accounts")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {

    @Inject
    private AccountService accountService;

    @POST
    public Response createAccount(@Valid @NotNull AccountRequest request) {
        AccountResponse created = accountService.createAccount(request);
        return Response.created(URI.create("/api/accounts/" + created.getId()))
                .entity(created)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getAccountById(@PathParam("id") Long id) {
        return accountService.getAccountById(id)
                .map(account -> Response.ok(account).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    public Response getAllAccounts() {
        return Response.ok(accountService.getAllAccounts()).build();
    }

    @GET
    @Path("/by-customer/{customerId}")
    public Response getAccountsByCustomerId(@PathParam("customerId") Long customerId) {
        return Response.ok(accountService.getAccountsByCustomerId(customerId)).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateAccount(@PathParam("id") Long id, @Valid @NotNull AccountRequest request) {
        AccountResponse updated = accountService.updateAccount(id, request);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAccount(@PathParam("id") Long id) {
        accountService.deleteAccount(id);
        return Response.noContent().build();
    }
}
