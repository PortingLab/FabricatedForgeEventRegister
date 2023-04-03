package org.portinglab.fabricated.fml;

import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.*;

public class FabricatedSPI {
    public static class ModFileScanData {
        private final Set<AnnotationData> annotations = new LinkedHashSet<>();

        public Set<AnnotationData> getAnnotations() {
            return annotations;
        }

        public record AnnotationData(Type annotationType, ElementType targetType, Type clazz, String memberName, Map<String, Object> annotationData) {}
    }
}
