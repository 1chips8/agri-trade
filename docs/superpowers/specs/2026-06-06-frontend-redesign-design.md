# Frontend Redesign Design

Date: 2026-06-06

## Goal

The frontend should reach a portfolio-ready standard. It does not need to be a production-grade ecommerce platform, but it must look and behave like a complete agricultural trading system instead of an API demo.

The redesigned frontend must make the three roles clear:

- Buyer: browse products, add to cart, place orders, pay, cancel, and confirm receipt.
- Merchant: apply for merchant access, manage products, and ship paid orders.
- Admin: approve merchants, approve products, manage categories, and view statistics.

Users should be able to understand what each page does without reading backend APIs, database rows, or source code.

## Current Problems

The current frontend has several structural problems:

- The top navigation is the same for every user. Buyers can see admin and dashboard entries.
- The router only checks whether the user is logged in, not whether the user has the right role.
- The cart page exposes internal IDs such as `productId` and `merchantId` instead of product names, prices, subtotals, and total amount.
- Order status is shown as raw enum values, and actions are not explained by page state.
- Merchant and admin pages behave more like test forms than complete business pages.
- Loading, empty, error, unauthorized, and forbidden states are inconsistent or missing.
- The frontend lacks a clear acceptance path for local testing and demonstration.

## Chosen Approach

Use a business-flow-first redesign with light shared frontend infrastructure.

Rejected alternatives:

- Style-only cleanup: too shallow, because it leaves role confusion and ID-heavy business pages intact.
- Full frontend architecture rewrite: cleaner long term, but too large for the current goal and likely to pull in unrelated backend changes.

The chosen approach keeps the Vue 3, Pinia, Vue Router, Axios, and Element Plus stack. It improves the information architecture, business pages, state labels, and validation surfaces while avoiding a large framework migration.

## Information Architecture

The app shell remains centered around `App.vue`, but navigation becomes role-aware.

Menu visibility:

- Guest: Products, Login.
- Buyer: Products, Cart, My Orders, Merchant Entry.
- Approved merchant: Products, Cart, My Orders, Merchant Center.
- Admin: Products, Admin, Dashboard.

Routes should declare required auth and roles through route metadata. Unauthorized users are redirected to login. Users without the required role are shown a forbidden state or redirected to a safe accessible route.

## Shared Frontend Building Blocks

Add small shared utilities and components only where they directly support the redesign:

- `constants/status.js`: Chinese labels and visual types for order, audit, sale, and merchant application statuses.
- `utils/format.js`: money, date/time, and fallback text formatting.
- Shared page state patterns: loading, empty, error, forbidden.
- Optional small components such as status tags, page headers, empty states, and product summary rows.

These should stay simple and match the existing Element Plus style.

## Buyer Experience

### Products

The product list should act as the buyer's entry point:

- Show product image, name, price, unit, origin, stock, and merchant context when available.
- Keep keyword search and add useful empty state copy.
- Disable purchasing when stock is zero.
- If a guest clicks add to cart, guide them to login.
- On successful add-to-cart, show a success message and make it easy to continue to cart.

### Cart

The cart must be understandable from the page:

- Show product image, product name, unit price, quantity, selected state, subtotal, stock, and operation buttons.
- Show selected item count and total amount.
- Keep receiver information separate from the item list.
- Provide empty cart state with a path back to products.

If the current backend cart API does not provide product details, the first implementation may enrich cart rows on the frontend by fetching product details by `productId`. A backend DTO can be added later if needed.

### Orders

Orders should display business states in Chinese:

- Pending payment, pending shipment, shipped, completed, and canceled.
- Only valid actions should appear for each state.
- Show order number, amount, receiver information, address, create time, and available actions.
- Display order item details when the backend supports them; otherwise clearly show the order-level information.

## Merchant Experience

Merchant center should be separated into three tabs:

- Entry status: application form for unsubmitted users; application status for submitted users; next-step guidance for approved merchants.
- Product management: publish products and view audit/sale status.
- Shipment management: list paid orders that require shipment and show valid shipment actions.

The product form should include name, category, price, stock, unit, origin, image, and description. The list should show audit status, sale status, price, stock, and valid actions based on state.

## Admin Experience

Admin pages should remain table-based but become complete enough for acceptance:

- Merchant review: store name, contact, phone, origin address, status, created time, approve, reject.
- Product review: product name, category, merchant, price, stock, status, approve, reject.
- Category management: create category and show existing categories.
- Operations should refresh the relevant list and provide clear success or failure feedback.
- Repeated invalid operations should be hidden by state-aware buttons.

## Dashboard

Dashboard is admin-only.

It should show:

- Order count.
- Sales amount.
- Product count.
- Merchant count if the backend provides it.
- A simple chart based on available statistics.

When data is unavailable, show a clear empty or unavailable state instead of silently rendering misleading zeroes.

## Visual Direction

The UI should feel like a clean business application for agricultural trade:

- Clear, trustworthy, and work-focused.
- Use green as an accent, not a single-color theme across the entire app.
- Avoid decorative landing-page treatment.
- Product pages can be more visual; admin and merchant pages should favor dense, readable operational layouts.
- Desktop is the main target, but common viewport changes must not break layout.

## Acceptance Criteria

The redesign is acceptable when the following checks pass:

- Guests can browse products.
- Guests cannot add products to cart without being guided to login.
- Buyers can add products to cart, change quantity, select items, submit an order, pay, cancel, and confirm receipt.
- Buyers cannot see admin-only navigation or pages.
- Merchant applicants can submit an application and see status.
- Approved merchants can create products, see audit/sale state, and ship eligible orders.
- Admins can approve merchants, approve products, manage categories, and view the dashboard.
- Core pages show loading, empty, error, and forbidden states where applicable.
- Direct route refresh works.
- Frontend build passes.
- A local acceptance test report documents what passed and what remains risky.

## Implementation Phases

1. Global foundation: role-aware navigation, route metadata, status labels, formatting utilities, and shared page states.
2. Buyer flow: products, cart, and orders.
3. Merchant and admin flow: merchant center, admin review pages, categories, and dashboard permissions.
4. Verification: build, local run, end-to-end manual/API-assisted acceptance, and report.

## Risks And Boundaries

- Cart and order pages may need additional backend data for the best display. The first version should use existing APIs where possible and only add backend DTOs when frontend enrichment is insufficient.
- The goal is portfolio readiness, not full production ecommerce behavior.
- Mobile optimization is secondary; desktop acceptance is primary.
- No unrelated framework migration or broad styling rewrite should be included in this phase.
