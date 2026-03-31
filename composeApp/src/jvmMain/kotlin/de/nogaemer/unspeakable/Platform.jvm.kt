package de.nogaemer.unspeakable

/**
 * Reports desktop Java runtime version for diagnostics and labels.
 */
class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}" 
}

/**
 * Binds shared platform lookup to JVM runtime metadata.
 * JVM: uses `java.version` system property.
 */
actual fun getPlatform(): Platform = JVMPlatform()