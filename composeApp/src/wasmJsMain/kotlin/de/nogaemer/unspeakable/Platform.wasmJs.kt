package de.nogaemer.unspeakable

/**
 * Reports Kotlin/Wasm runtime identity for web diagnostics.
 */
class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

/**
 * Binds shared platform lookup to the Kotlin/Wasm web label.
 * Wasm: returns a WebAssembly-target runtime descriptor.
 */
actual fun getPlatform(): Platform = WasmPlatform()