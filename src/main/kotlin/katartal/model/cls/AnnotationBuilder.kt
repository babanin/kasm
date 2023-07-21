package katartal.model.cls

class AnnotationBuilder(name: String, access: ClassAccess) 
    : CommonClassBuilder<AnnotationBuilder>(name, access + ClassAccess.INTERFACE + ClassAccess.ABSTRACT + ClassAccess.ANNOTATION) {
}