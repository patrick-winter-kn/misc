package de.unikonstanz.winter.util.node.open;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class OpenFactory extends NodeFactory<OpenModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public OpenModel createNodeModel() {
        return new OpenModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<OpenModel> createNodeView(int viewIndex, OpenModel nodeModel) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new OpenDialog();
    }

}
