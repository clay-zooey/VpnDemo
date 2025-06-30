# VPN Demo App

A minimal Android VPN application demonstrating basic VPN service implementation using Jetpack Compose and modern Android architecture patterns.

## Architecture and Key Design Decisions

The app follows a clean architecture approach with clear separation of concerns:

- **MVVM Pattern**: Uses `VpnViewModel` as the single source of truth for UI state management, communicating with the UI layer through Kotlin Flows (`StateFlow` for state, `SharedFlow` for events).

- **VpnService Lifecycle Management**: The VPN service lifecycle is managed through a combination of Android's `VpnService` framework and custom event handling:
  - Service starts via `startService()` with explicit intent actions
  - Service stops via `ACTION_DISCONNECT` intent or system termination
  - State synchronization between service and UI through `VpnEventBus` singleton
  - Proper cleanup in `onDestroy()` to prevent resource leaks

- **Permission Handling**: Implements Android's VPN permission flow using `VpnService.prepare()` and `ActivityResultLauncher` for user consent, ensuring compliance with Android security requirements.

- **Event-Driven Communication**: Uses a singleton `VpnEventBus` with `SharedFlow` for service-to-UI communication, allowing the ViewModel to react to service state changes and error conditions.

## Added Libraries

- **Hilt**: Dependency injection framework for managing object creation and dependencies

## Development Assumptions

- Minimal VPN Implementation: The VPN service creates a basic tunnel interface without actual traffic routing or encryption - this is a demonstration app, not a production VPN solution.
- User Consent Required: Assumes VPN permission must be granted by the user before service can start, following Android security best practices.
- Service Persistence: Uses `START_STICKY` return value to ensure service restarts if killed by the system, maintaining VPN connection state.