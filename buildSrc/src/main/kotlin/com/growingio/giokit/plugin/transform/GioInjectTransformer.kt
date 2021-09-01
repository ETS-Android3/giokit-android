package com.growingio.giokit.plugin.transform

import com.didiglobal.booster.transform.TransformContext
import com.growingio.giokit.plugin.utils.GioConfigUtils
import com.growingio.giokit.plugin.utils.className
import com.growingio.giokit.plugin.utils.println
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * <p>
 *
 * @author cpacm 2021/8/23
 */
class GioInjectTransformer : ClassTransformer {

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        val className = klass.className
        if (className == "com.growingio.giokit.GioKitImpl") {
            klass.methods.find { it.name == "initGioKitConfig" }
                .let { methodNode ->
                    methodNode?.instructions?.insert(createPluginConfigInsnList())
                }
        }

        return klass
    }

    private fun createPluginConfigInsnList(): InsnList {
        return with(InsnList()) {
            //new HashMap
            add(TypeInsnNode(Opcodes.NEW, "java/util/HashMap"))
            add(InsnNode(Opcodes.DUP))
            add(MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false))
            //保存变量
            add(VarInsnNode(Opcodes.ASTORE, 0))
            //put("hasGioPlugin",true)
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("gioPlugin"))
            add(InsnNode(if (GioConfigUtils.hasGioPlugin) Opcodes.ICONST_1 else Opcodes.ICONST_0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "java/lang/Boolean",
                    "valueOf",
                    "(Z)Ljava/lang/Boolean;",
                    false
                )
            )
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    true
                )
            )
            add(InsnNode(Opcodes.POP))

            //put("xmlScheme","")
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("xmlScheme"))
            add(LdcInsnNode(GioConfigUtils.xmlScheme))
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    false
                )
            )
            add(InsnNode(Opcodes.POP))

            //put("gioDepend","")
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(LdcInsnNode("gioDepend"))
            add(LdcInsnNode(GioConfigUtils.getGioDepend()))
            add(
                MethodInsnNode(
                    Opcodes.INVOKEINTERFACE,
                    "java/util/Map",
                    "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    false
                )
            )
            add(InsnNode(Opcodes.POP))

            //将配置放入GioPluginConfig中 com.growingio.giokit.hook.GioPluginConfig
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/growingio/giokit/hook/GioPluginConfig",
                    "inject",
                    "(Ljava/util/Map;)V",
                    false
                )
            )

            this
        }
    }
}