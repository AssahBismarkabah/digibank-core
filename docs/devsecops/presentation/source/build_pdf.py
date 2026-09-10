from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm
from reportlab.platypus import (
    Image,
    KeepTogether,
    PageBreak,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)


ROOT = Path(__file__).resolve().parents[4]
OUT = ROOT / "docs/devsecops/presentation/output/digibank-workshop3-dast-draft.pdf"
DIAGRAM = ROOT / "docs/devsecops/presentation/assets/diagrams/07_microservices_topology.png"
MAKE_UP = ROOT / "docs/devsecops/presentation/assets/screenshots/make-up.png"
CONTAINERS = ROOT / "docs/devsecops/presentation/assets/screenshots/containers-servies.png"
MAKE_SMOKE = ROOT / "docs/devsecops/presentation/assets/screenshots/make-smoke.png"
POSTMAN_FUNCTIONAL = ROOT / "docs/devsecops/presentation/assets/screenshots/postman-happypath.png"
POSTMAN_SECURITY = ROOT / "docs/devsecops/presentation/assets/screenshots/postmanrun-output-1.png"
POSTMAN_SECURITY_DETAIL = ROOT / "docs/devsecops/presentation/assets/screenshots/postman-run-output2.png"
SESSION = ROOT / "docs/devsecops/presentation/assets/screenshots/session-behavior.png"
HEADERS = ROOT / "docs/devsecops/presentation/assets/screenshots/information-exposure.png"
NEWMAN = ROOT / "docs/devsecops/presentation/assets/screenshots/newman-scan.png"
NEWMAN_REPORTS = ROOT / "docs/devsecops/presentation/assets/screenshots/newman-directory-result.png"
ZAP = ROOT / "docs/devsecops/presentation/assets/screenshots/zap-scan.png"
ZAP_ALERTS = ROOT / "docs/devsecops/presentation/assets/screenshots/alert-zap-scan.png"


styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name="CoverKicker", parent=styles["Normal"], fontSize=9, leading=12, textColor=colors.HexColor("#555555"), spaceAfter=16, tracking=1.5))
styles.add(ParagraphStyle(name="CoverTitle", parent=styles["Title"], fontSize=28, leading=31, spaceAfter=12))
styles.add(ParagraphStyle(name="CoverLead", parent=styles["Normal"], fontSize=15, leading=21, spaceAfter=9))
styles.add(ParagraphStyle(name="IntroBody", parent=styles["BodyText"], fontSize=12, leading=18, spaceAfter=13))
styles.add(ParagraphStyle(name="H2Black", parent=styles["Heading2"], fontSize=18, leading=22, textColor=colors.black, spaceBefore=10, spaceAfter=8, borderPadding=3))
styles.add(ParagraphStyle(name="BodyBlack", parent=styles["BodyText"], fontSize=10.5, leading=15, spaceAfter=7))
styles.add(ParagraphStyle(name="SmallGray", parent=styles["BodyText"], fontSize=8.5, leading=11, textColor=colors.HexColor("#555555"), spaceAfter=6))
styles.add(ParagraphStyle(name="TableHeader", parent=styles["BodyText"], fontSize=9.5, leading=12, textColor=colors.white, spaceAfter=0))
styles.add(ParagraphStyle(name="Callout", parent=styles["BodyText"], fontSize=10.5, leading=15, leftIndent=8, borderColor=colors.black, borderWidth=1, borderPadding=8, backColor=colors.HexColor("#eeeeee"), spaceBefore=8, spaceAfter=10))
styles.add(ParagraphStyle(name="CodeBlock", parent=styles["Code"], fontSize=8.5, leading=12, textColor=colors.white, backColor=colors.black, borderPadding=9, spaceBefore=7, spaceAfter=10))

SECTION_GAP = Spacer(1, 6 * mm)


def P(text, style="BodyBlack"):
    return Paragraph(text, styles[style])


def table(rows, widths):
    converted = [[P(str(cell), "TableHeader" if row == 0 else "SmallGray") for cell in r] for row, r in enumerate(rows)]
    t = Table(converted, colWidths=widths, repeatRows=1)
    t.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), colors.black),
        ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
        ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#999999")),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 6),
        ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 5),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
    ]))
    return t


story = []
story += [Spacer(1, 62 * mm), P("UCC 152-2 - Introduction to Security in DevOps", "CoverKicker"), P("DigiBank Dynamic Security Analysis", "CoverTitle"), P("Runtime security validation of the DigiBank API.", "CoverLead"), PageBreak()]

story += [P("1. Why DAST is needed", "H2Black"), P("Static analysis examines source code and configuration before execution. Dynamic analysis complements it by interacting with the application while it is running. This matters because a system can compile, pass unit tests, and still expose an API route without authentication, return excessive technical detail, accept invalid input, or handle sessions incorrectly.", "IntroBody"), P("<b>DAST checks what an external client can actually observe, not only what the source code appears to intend.</b>", "IntroBody"), P("The sequence is: establish runtime behavior, identify security weaknesses, apply targeted remediation, and replay the same scenarios to demonstrate the result.", "IntroBody"), PageBreak()]

story += [P("2. Application under test", "H2Black"), P("The external client does not call each business service directly. The API Gateway is the security boundary and the single entry point for the business API. Behind it, the customer, account, transaction, compliance, and notification services run as separate Compose services."), Image(str(DIAGRAM), width=175 * mm, height=85 * mm), P("Figure 1. Runtime topology under test: gateway, business services, platform services, Compose host and persistence boundaries.", "SmallGray"), table([["Surface", "What is tested"], ["Gateway", "Authentication, authorization, headers, cache behavior, error responses and route protection."], ["Business APIs", "Customer, account, transaction and compliance operations through gateway routing."], ["Runtime behavior", "Malformed requests, invalid values, missing resources, session behavior and information disclosure."], ["Delivery path", "Build, Compose startup, readiness, Newman replay and ZAP scanning."]], [42 * mm, 130 * mm]), PageBreak()]

story += [P("3. Reproducible test environment", "H2Black"), P("The environment is deliberately reproducible. Maven verifies the implementation, Docker Compose starts the complete topology, smoke checks verify the endpoints, and stack validation confirms that the expected containers are running before DAST begins."), P("<font name='Courier'>make clean<br/>make build<br/>make image-build<br/>make up<br/>make smoke<br/>make validate-stack<br/>make newman-scan<br/>make zap-scan</font>"), SECTION_GAP]

story += [P("3.1 Starting the application", "H2Black"), P("The Compose startup creates the complete local runtime. The screenshot shows the command used to start the services and the resulting container state. This establishes the execution context before any security request is sent."), Image(str(MAKE_UP), width=175 * mm, height=56 * mm), P("The container view confirms that the gateway, business services, databases, discovery server and config server are present in the same runtime topology.", "SmallGray"), Image(str(CONTAINERS), width=175 * mm, height=66 * mm), PageBreak()]

story += [P("3.2 Smoke validation", "H2Black"), P("Smoke validation checks the public health endpoint, gateway business routes, direct internal service endpoints, discovery registry and config server. It is not the security campaign; it proves that the application is alive and that later DAST failures can be interpreted as application behavior rather than startup failure."), Image(str(MAKE_SMOKE), width=175 * mm, height=100 * mm), P("The successful result is the gate before Newman and ZAP.", "SmallGray"), PageBreak()]

story += [P("4. Baseline and remediation decision", "H2Black"), P("The baseline establishes the behavior before the gateway security boundary was introduced. The important observations were unauthenticated business routes, no gateway role distinction, incomplete response hardening, and no explicit cache policy for sensitive API responses."), P("The remediation uses stateless HTTP Basic authentication at the gateway because it is simple to reproduce in the workshop and preserves the existing service topology. It is not presented as the production identity architecture."), table([["Request condition", "Expected result after remediation"], ["No credentials or invalid credentials", "401 Unauthorized"], ["Valid USER on normal banking API", "Request is permitted"], ["Valid USER on compliance API", "403 Forbidden"], ["Valid ADMIN on compliance API", "Request is permitted"], ["Authenticated request", "No server session or session cookie"]], [70 * mm, 102 * mm]), PageBreak()]

story += [P("4.1 Authentication and authorization in Postman", "H2Black"), P("The security collection makes the access rules visible through actual requests. The run shows unauthenticated requests returning 401, normal USER access returning 200, USER access to compliance returning 403, and ADMIN access to compliance returning 200."), Image(str(POSTMAN_SECURITY), width=175 * mm, height=126 * mm), P("The request results correspond directly to the gateway rules: authentication establishes identity, and authorization decides whether that identity may use the route.", "SmallGray"), PageBreak()]

story += [P("4.2 Validation, errors and information exposure", "H2Black"), P("The security collection also checks malformed JSON and missing resources. These cases return controlled 4xx responses without stack traces or internal implementation details. The same collection verifies the response headers on the health endpoint."), Image(str(POSTMAN_SECURITY_DETAIL), width=175 * mm, height=126 * mm), P("The run shows the validation and error-handling assertions passing, including the controlled 400 and 404 responses.", "SmallGray"), PageBreak()]

story += [P("4.3 Headers and stateless behavior", "H2Black"), P("The headers check verifies browser and response protections. The session check verifies that an authenticated request does not create a server session or return a session cookie."), Image(str(HEADERS), width=130 * mm, height=67 * mm), Image(str(SESSION), width=130 * mm, height=83 * mm), P("These screenshots show the two related controls: hardened responses and stateless authentication.", "SmallGray"), PageBreak()]

story += [P("5. Runtime checks and scan results", "H2Black"), P("Newman expresses the expected security behavior as repeatable assertions. The security collection is divided into authentication, authorization, functional regression, input validation, error handling, information exposure, session behavior, and remediation verification."), table([["Campaign", "Result"], ["Functional Newman collection", "24 requests, 74 assertions, 0 failures."], ["Security Newman collection", "12 requests, 19 assertions, 0 failures."], ["OWASP ZAP baseline", "0 High, 1 Medium, 0 Low, 1 Informational."]], [65 * mm, 107 * mm]), P("The remaining Medium ZAP alert is the expected local warning that HTTP Basic credentials are sent over plain HTTP. It is acceptable only in this local educational environment and requires TLS in production. The Informational observation concerns public static-resource cacheability; authenticated API responses receive <font name='Courier'>Cache-Control: no-store, private</font>.", "BodyBlack"), SECTION_GAP]

story += [P("5.1 Functional regression in Postman", "H2Black"), P("The functional collection checks the banking flows after the security changes: customer creation and retrieval, account creation, transactions and compliance operations. This confirms that the gateway protection did not break normal application behavior."), Image(str(POSTMAN_FUNCTIONAL), width=175 * mm, height=126 * mm), P("The collection run shows the banking requests and their passing assertions in the local environment.", "SmallGray"), PageBreak()]

story += [P("5.2 Newman security replay", "H2Black"), P("Newman repeats the same security collection without relying on the Postman interface. The summary is the reproducible command-line result used locally and in CI."), Image(str(NEWMAN), width=175 * mm, height=128 * mm), P("The security replay completed 12 requests and 19 assertions with zero failures.", "SmallGray"), Image(str(NEWMAN_REPORTS), width=150 * mm, height=42 * mm), P("The report directory contains separate functional and security JSON results.", "SmallGray"), PageBreak()]

story += [P("5.3 OWASP ZAP scan", "H2Black"), P("ZAP scans the externally visible gateway surface and complements the targeted Postman assertions. The scan checks the running application for observable response, header and authentication weaknesses that are not covered by a small set of targeted requests."), P("The scan completed with no High or Low alerts. The remaining findings are reviewed below and are interpreted against the local test environment rather than treated as an unexplained score."), SECTION_GAP]

story += [P("5.4 ZAP alert summary", "H2Black"), P("The ZAP summary reports zero High alerts, one Medium alert, zero Low alerts and one Informational alert. The remaining Medium alert is the local plain-HTTP warning associated with HTTP Basic authentication. It is recorded and explained in the report."), Image(str(ZAP_ALERTS), width=175 * mm, height=87 * mm), PageBreak()]

story += [P("6. CI validation", "H2Black"), P("The CI workflow repeats the same validation chain after the application is built. Each step has a clear purpose: start the runtime, prove readiness, verify the exposed routes, replay the security assertions, and scan the gateway surface."), table([["Pipeline step", "Purpose"], ["Build and tests", "Compile the services and run the automated test suite."], ["Compose and readiness", "Start the complete runtime and wait for the required endpoints."], ["Smoke validation", "Confirm gateway, business APIs and platform services respond."], ["Newman", "Run the versioned functional and security request assertions."], ["ZAP", "Run an independent baseline scan against the running gateway."], ["Report upload", "Keep Newman and ZAP results available when a check fails."]], [55 * mm, 117 * mm]), P("Newman and ZAP remain separate checks: Newman verifies expected security behavior, while ZAP performs broader automated observations of the exposed HTTP surface.", "SmallGray"), SECTION_GAP]

story += [P("7. Deliverable mapping", "H2Black"), table([["Objective", "Demonstrated by"], ["A3.1", "SAST/DAST explanation and runtime validation method."], ["A3.2", "Versioned Postman functional and security collections."], ["A3.3", "Assertions for status codes, headers, response bodies and error messages."], ["A3.4", "Basic authentication, role checks and stateless-session checks."], ["A3.5", "Baseline Newman behavior and ZAP observations."], ["A3.6", "Gateway authentication, authorization, headers, cache and error hardening."], ["A3.7", "Replayed Newman and ZAP validation after remediation."], ["A3.8", "Technical report, reports, screenshots and CI artifacts."]], [25 * mm, 147 * mm]), P("<b>The result is a documented chain from runtime observation, to risk interpretation, to remediation, to reproducible revalidation.</b>")]


def footer(canvas, doc):
    canvas.saveState()
    canvas.setStrokeColor(colors.black)
    canvas.setLineWidth(1)
    canvas.line(17 * mm, 13 * mm, 193 * mm, 13 * mm)
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(colors.HexColor("#555555"))
    canvas.drawString(17 * mm, 8 * mm, "DigiBank - Dynamic Security Analysis")
    canvas.drawRightString(193 * mm, 8 * mm, str(doc.page))
    canvas.restoreState()


doc = SimpleDocTemplate(str(OUT), pagesize=A4, rightMargin=17 * mm, leftMargin=17 * mm, topMargin=17 * mm, bottomMargin=20 * mm)
doc.build(story, onFirstPage=footer, onLaterPages=footer)
print(OUT)
