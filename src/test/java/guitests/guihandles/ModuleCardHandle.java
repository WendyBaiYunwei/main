package guitests.guihandles;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;

/**
 * Provides a handle to a module card in the module list panel.
 */
public class ModuleCardHandle extends NodeHandle<Node> {
    private static final String ID_FIELD_ID = "#id";
    private static final String NAME_FIELD_ID = "#name";
    private static final String CODE_FIELD_ID = "#code";
    private static final String CREDITS_FIELD_ID = "#credits";
    private static final String TAGS_FIELD_ID = "#tags";
    private static final String COREQUISITES_FIELD_ID = "#corequisites";

    private final Label idLabel;
    private final Label nameLabel;
    private final Label codeLabel;
    private final Label creditsLabel;
    private final List<Label> tagLabels;
    private final Label corequisitesLabel;

    public ModuleCardHandle(Node cardNode) {
        super(cardNode);

        idLabel = getChildNode(ID_FIELD_ID);
        nameLabel = getChildNode(NAME_FIELD_ID);
        codeLabel = getChildNode(CODE_FIELD_ID);
        creditsLabel = getChildNode(CREDITS_FIELD_ID);

        Region tagsContainer = getChildNode(TAGS_FIELD_ID);
        tagLabels = tagsContainer
                .getChildrenUnmodifiable()
                .stream()
                .map(Label.class::cast)
                .collect(Collectors.toList());

        corequisitesLabel = getChildNode(COREQUISITES_FIELD_ID);
    }

    public String getId() {
        return idLabel.getText();
    }

    public String getName() {
        return nameLabel.getText();
    }

    public String getCode() {
        return codeLabel.getText();
    }

    public String getCredits() {
        String credits = creditsLabel.getText();
        if (credits.startsWith("Modular Credits: ")) {
            credits = credits.substring("Modular Credits: ".length());
        }
        return credits;
    }

    public List<String> getTags() {
        return tagLabels
                .stream()
                .map(Label::getText)
                .collect(Collectors.toList());
    }

    public String getCorequisites() {
        String corequisites = corequisitesLabel.getText();
        if (corequisites.startsWith("Co-requisites: ")) {
            corequisites = corequisites.substring("Co-requisites: ".length());
        }

        if (corequisites.equals("None")) {
            corequisites = "";
        }
        return corequisites;
    }

    /**
     * Returns true if this handle contains {@code module}.
     */
    public boolean equals(Module module) {
        return getName().equals(module.getName().fullName)
                && getCode().equals(module.getCode().value)
                && getCredits().equals(module.getCredits().value)
                && getTags().equals(module.getTags().stream()
                    .map(tag -> tag.tagName)
                    .sorted()
                    .collect(Collectors.toList()))
                && getCorequisites().equals(module.getCorequisites().stream().map(Code::toString)
                    .collect(Collectors.joining(", ")));
    }
}
