# DigiBank DAST – Baseline Findings

This directory holds the **pre-hardening** ZAP report generated during the first scan.

## How the baseline is created

1. Start the dev stack: `make up` (from `digibank-parent/`)
2. Run the scan: `make zap-scan`
3. Copy the generated report here:
   ```bash
   cp ../zap-report.html dast/reports/baseline/baseline-zap-report.html
   cp ../zap-report.json dast/reports/baseline/baseline-zap-report.json
   ```
4. Commit the baseline reports so future runs can be compared against them.

## After hardening

After each hardening ticket is merged:
- Re-run `make zap-scan` to get the post-fix report
- Compare alert counts and categories between the new report and the files here
- Document the delta in the PR description

The reduction in ZAP alerts (especially for headers and error disclosure) is the
evidence of remediation effectiveness required by §4.9 of the DAST guide.
