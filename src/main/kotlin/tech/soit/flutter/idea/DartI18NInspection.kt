package tech.soit.flutter.idea

import com.intellij.codeInspection.LocalInspectionTool

class DartI18NInspection : LocalInspectionTool() {

//    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
//        println("checkFile $file")
//        dumpAst(file)
//        if (file.language != DartLanguage.INSTANCE) {
//            return null
//        }
//        val list = mutableListOf<ProblemDescriptor>()
//        file.accept(object : DartRecursiveVisitor() {
//            override fun visitCallExpression(o: DartCallExpression) {
//                super.visitCallExpression(o)
//                if (o.expression.text == "Text") {
//                    println("create text : ${o.text}")
//                    val args = o.arguments ?: return
//                    val problemDescriptor = manager.createProblemDescriptor(args, "fuck", null as LocalQuickFix?, ProblemHighlightType.ERROR, false)
//                    list.add(problemDescriptor)
//                }
//            }
//        })
//        return list.toTypedArray()
//    }


}