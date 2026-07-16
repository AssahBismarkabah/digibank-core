package com.digibank.transaction.resource;

import com.digibank.transaction.dto.TransactionRequest;
import com.digibank.transaction.dto.TransactionResponse;
import com.digibank.transaction.service.TransactionService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/transactions")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    private TransactionService transactionService;

    @POST
    public Response createTransaction(@Valid @NotNull TransactionRequest request) {
        TransactionResponse created = transactionService.createTransaction(request);
        return Response.created(URI.create("/api/transactions/" + created.getId()))
                .entity(created)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getTransactionById(@PathParam("id") Long id) {
        return transactionService.getTransactionById(id)
                .map(transaction -> Response.ok(transaction).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    public Response getAllTransactions() {
        return Response.ok(transactionService.getAllTransactions()).build();
    }

    @GET
    @Path("/by-account/{accountId}")
    public Response getTransactionsByAccountId(@PathParam("accountId") Long accountId) {
        return Response.ok(transactionService.getTransactionsByAccountId(accountId)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTransaction(@PathParam("id") Long id) {
        transactionService.deleteTransaction(id);
        return Response.noContent().build();
    }
}
