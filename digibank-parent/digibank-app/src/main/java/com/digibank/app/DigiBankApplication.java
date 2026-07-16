package com.digibank.app;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Jakarta REST (JAX-RS) application entry point.
 *
 * Registers all REST resources via package scanning:
 * - /api/customers    -> CustomerResource
 * - /api/accounts     -> AccountResource
 * - /api/transactions -> TransactionResource
 * - /api/compliance   -> ComplianceResource
 *
 * No web.xml required -- Jakarta EE 10 discovers this class automatically.
 */
@ApplicationPath("/api")
public class DigiBankApplication extends Application {
}
