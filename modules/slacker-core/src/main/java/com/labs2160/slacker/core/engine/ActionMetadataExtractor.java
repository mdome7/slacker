package com.labs2160.slacker.core.engine;

import java.lang.annotation.Annotation;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.annotation.ActionDescription;

/**
 * Utility class to inspect an action class and populate
 * an ActionMetadata object.
 *
 */
public final class ActionMetadataExtractor {

    private ActionMetadataExtractor() {}

    public static ActionMetadata extract(Class<? extends Action> actionClass) {
        for (Annotation annot: actionClass.getAnnotations()) {
            if (annot instanceof ActionDescription) {
                ActionDescription ad = (ActionDescription) annot;
                // TODO: add config params to metadata
                return new ActionMetadata(actionClass, ad.name(), ad.description(), ad.argsSpec(), ad.argsExample());
            }
        }
        return new ActionMetadata(actionClass);
    }

}
