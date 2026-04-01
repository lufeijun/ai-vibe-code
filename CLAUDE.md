# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Vue 3 admin dashboard demo built with Element Plus, featuring a complete authentication flow and modular layout. Includes login page, dashboard overview, user management, and system settings modules. Uses modern Vue 3 ecosystem tools: TypeScript, Vue Router 4, Pinia state management, and Vite build tool.

## Development Commands

Standard npm scripts from `package.json`:

- `npm run dev` - Start development server at http://localhost:3000
- `npm run build` - Type-check with vue-tsc then build for production
- `npm run preview` - Locally preview production build

No linting or testing commands are currently configured.

## Architecture and Key Patterns

### Tech Stack
- **Vue 3** - Composition API with `<script setup>` syntax
- **TypeScript** - Type safety
- **Vue Router 4** - Route management with route-based code splitting
- **Pinia** - Centralized state management
- **Element Plus** - UI component library with auto-import
- **Vite** - Build tool with HMR support
- **Axios** - HTTP client (installed but not currently used)

### Project Structure
- `src/views/` - Page-level Vue components (login, dashboard, user management, system settings)
- `src/layouts/` - Layout components (`DashboardLayout.vue` for authenticated areas)
- `src/router/` - Route definitions and navigation guards
- `src/stores/` - Pinia stores (`auth.ts` for authentication)
- `src/assets/styles/` - Global SCSS styles
- Auto-generated type files: `auto-imports.d.ts` and `components.d.ts`

### Authentication Flow
1. Login state managed in `stores/auth.ts` using Pinia
2. State persisted to `localStorage` (keys: `isLoggedIn`, `username`, `remember`)
3. Route guard in `router/index.ts` checks `localStorage` directly for authentication
4. Unauthenticated users accessing protected routes are redirected to `/login`
5. Authenticated users accessing `/login` are redirected to `/dashboard`

**Important**: Both the auth store and route guard interact with `localStorage` - ensure synchronization when modifying authentication logic.

### Layout Pattern
- Authenticated routes use `DashboardLayout.vue` layout component
- Layout features top navigation menu (primary modules) and dynamic side menu (secondary routes)
- Side menu configuration is centralized in `DashboardLayout.vue` based on active top menu
- Route structure uses nested routes with layout as parent component

### Routing Configuration
Routes defined in `router/index.ts` with metadata:
- `requiresAuth: boolean` - Whether route requires authentication
- `title: string` - Page title for browser tab

Route structure:
- `/login` - Public login page
- `/dashboard` - Dashboard module with nested routes (`/dashboard/overview`, `/dashboard/stats`)
- `/user` - User management module with nested routes (`/user/list`, `/user/role`)
- `/system` - System settings module with nested routes (`/system/settings`, `/system/logs`)

### Key Implementation Details
- **Path alias**: `@` points to `src/` directory (configured in `vite.config.ts`)
- **Auto-imports**: Via `unplugin-auto-import` and `unplugin-vue-components` for Element Plus components and Vue composables
- **Element Plus CSS**: Imported as CSS only (no theme customization) via `importStyle: 'css'` in vite.config.ts
- **Form validation**: Uses Element Plus form validation rules with `el-form` components
- **Responsive design**: Custom CSS using Flexbox/Grid for responsive layouts
- **Icon usage**: Element Plus icons imported from `@element-plus/icons-vue`
- **Styling**: SCSS with scoped styles using `<style scoped>`

### Component Conventions
- Page components use `<script setup lang="ts">` with TypeScript
- Element Plus components are auto-imported (no manual imports needed)
- Icons are manually imported from `@element-plus/icons-vue`
- SCSS styles use `<style scoped>` for component isolation
- Form components use `el-form` with validation rules, referenced via `ref`
- Layout components use computed properties for active menu states based on route

## Development Notes

- Development server runs on port 3000 (configurable in `vite.config.ts`)
- No ESLint or Prettier configuration currently
- No testing framework configured
- Login form accepts any user input (no real authentication validation)
- Project uses Chinese language for UI labels and comments
- **API integration**: Axios is installed but not used; planned for future backend integration
- **Extension plans**: Refer to README.md for suggested feature extensions