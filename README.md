# Learning Dashboard Mobile Application

A production-grade, offline-first Android application built with **Kotlin**, **Jetpack Compose**, and **Clean Architecture (MVVM / UDF)**.

---

### 1. Architecture: Why did you choose your architecture?

We chose **Clean Architecture with Unidirectional Data Flow (UDF / MVI-style MVVM)** organized across a multi-module Gradle layout (`:core:domain`, `:core:database`, `:core:network`, `:core:data`, `:core:ui`, `:feature:*`):

* **Domain Purity & Fast Testing:** `:core:domain` is a pure Kotlin JVM module with zero Android framework dependencies. Core rules ([`ProgressCalculator`](core/domain/src/main/kotlin/com/learning/dashboardmobileapp/core/domain/util/ProgressCalculator.kt), validation) execute and test in milliseconds without emulators or Robolectric.
* **Single Source of Truth (SSOT):** The UI strictly observes immutable `StateFlow` streams from local storage. UI rendering is completely decoupled from network latency, transient disconnections, and API payloads.
* **Feature Isolation & Build Performance:** Features (`:feature:auth`, `:feature:dashboard`, `:feature:coursedetail`) depend only on domain use cases and design tokens. This enforces encapsulation, prevents circular dependencies, and enables parallel Gradle build execution.

---

### 2. Offline Support: How are you storing and loading offline data?

* **Storage Engine:** Courses and lessons are persisted locally in SQLite (`SQLiteOpenHelper`) in relational tables (`courses`, `lessons`) with indexed foreign keys.
* **Reactive Cache-First Loading:** When any screen opens, the repository emits the cached data immediately via Kotlin `Flow`. Users never stare at empty screens or blocking spinners if data was loaded once.
* **Sync & Local Preservation:** When online, a background fetch hits the API and invokes `upsertPreservingLocalCompletion`. This updates course metadata while preserving lessons marked completed by the user while offline. If network calls fail or the device is offline, the exception is caught and the local cache continues serving the UI seamlessly.

---

### 3. Security: Where would you store authentication tokens in a production application?

* **Hardware-Backed Storage:** Store JWT access and refresh tokens using **EncryptedSharedPreferences** (Jetpack Security) backed by the **Android Keystore System** (AES-256-GCM / TEE or StrongBox Keymaster), ensuring tokens cannot be extracted from rooted devices or backup images.
* **Secure In-Memory Access:** Decrypt tokens only into an in-memory session singleton (`SessionManager`) injected via an OkHttp/Ktor `Interceptor` and `Authenticator` for token auto-refresh. Never persist raw tokens in plaintext SharedPreferences, SQLite/Room, or device logs.
* **Biometric Step-Up:** For high-security environments, bind Keystore key usage to `BiometricPrompt` with `setUserAuthenticationRequired(true)`.

---

### 4. Scale: If this application had 1M users + hundreds of courses, 3–5 improvements:

1. **Jetpack Paging 3 with `RemoteMediator`:** Replace full-list queries with incremental cursor/page loading (e.g., 20 courses per chunk) to eliminate memory spikes and reduce database query execution time.
2. **WorkManager Delta Sync Engine:** Replace monolithic refreshes with `WorkManager` background jobs syncing only modified items using `ETag` / `last_modified_timestamp`, and queueing offline user actions for retry with exponential backoff.
3. **CDN Asset Optimization & Disk Caching:** Serve media through a CDN (Cloudflare/CloudFront) with WebP/AVIF format transcoding and Coil disk LRU cache to conserve mobile bandwidth.
4. **Full-Text Search (SQLite FTS5):** Integrate SQLite FTS5 virtual tables to provide sub-millisecond offline search and filtering across hundreds of course titles, lessons, and instructors.
5. **Observability & RUM:** Add OpenTelemetry/Firebase Performance tracing to monitor Time-to-Initial-Display (TTID), frame drops, and database transaction latency in production.

---

### 5. Second Platform: iOS/macOS Implementation Strategy

* **UI & Navigation:** **SwiftUI** with `NavigationStack` and `@Environment`, matching Jetpack Compose declarative reactivity.
* **Architecture & State:** Clean Architecture + MVVM using the **Observation framework (`@Observable`)** in iOS 17+ (or `Combine` `CurrentValueSubject` on earlier versions) to mirror Kotlin's `StateFlow` and UDF patterns.
* **Offline Persistence:** **SwiftData** (or **GRDB.swift** / CoreData) providing local SQLite persistence with `@Query` macros for reactive data binding.
* **Secure Token Storage:** **iOS Keychain Services** (`kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly`) protected by the Secure Enclave and LocalAuthentication (Face ID/Touch ID).
* **Networking:** `URLSession` with Swift `async/await` and custom `AuthenticationDelegate` for automatic 401 token refresh.
* **Alternative (KMP):** Compile `:core:domain` and `:core:data` into an iOS XCFramework via **Kotlin Multiplatform (KMP)** to share 100% of business logic, models, calculators, and SQLite caching between Android and iOS.