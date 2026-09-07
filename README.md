# core-payments-platform

A realistic, multi-service fintech target codebase — the "brownfield" application
used to demonstrate agent-driven code changes in an enterprise-style system.

This codebase is deliberately independent of both:
- the **agentic core** (a separate Python/LangGraph component that reads this
  codebase and proposes changes to it), and
- the **trustworthy-agentic-framework** (a separate governance/reliability
  service the agentic core calls to validate its proposed changes before
  they're applied here)

Neither the agent nor the framework is part of this repository. This is
intentional — it's what makes the "any agent, validated by any governance
layer" claim demonstrable rather than just asserted.

## Services
- `account-service` — owns customer accounts and balances