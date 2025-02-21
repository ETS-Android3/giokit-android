package com.growingio.giokit.plugin.transform

import com.growingio.giokit.plugin.utils.GioTransformContext
import com.growingio.giokit.plugin.utils.GioTransformListener
import org.objectweb.asm.tree.ClassNode
import java.io.File

/**
 * <p>
 *
 * @author cpacm 2021/8/18
 */
interface ClassTransformer : GioTransformListener {

    val name: String
        get() = javaClass.simpleName

    fun getReportDir(context: GioTransformContext): File =
        File(File(context.reportsDir, name), context.name)

    fun getReport(context: GioTransformContext, name: String): File {
        val report: File by lazy {
            val dir = getReportDir(context)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, name)
            if (!file.exists()) {
                file.createNewFile()
            }
            file
        }
        return report
    }

    /**
     * Transform the specified class node
     *
     * @param context The transform context
     * @param klass The class node to be transformed
     * @return The transformed class node
     */
    fun transform(context: GioTransformContext, klass: ClassNode) = klass

    fun transformLatest(context: GioTransformContext, klass: ClassNode): ClassNode {
        return klass
    }

    fun isTransformLatest(klass: ClassNode) = false
}