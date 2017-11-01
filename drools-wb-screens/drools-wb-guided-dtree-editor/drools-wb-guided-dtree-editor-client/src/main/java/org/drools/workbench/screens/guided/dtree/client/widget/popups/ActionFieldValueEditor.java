/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtree.client.widget.popups;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionFieldValue;
import org.drools.workbench.screens.guided.dtree.client.resources.i18n.GuidedDecisionTreeConstants;
import org.drools.workbench.screens.guided.dtree.client.widget.utils.ValueUtilities;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.mvp.ParameterizedCommand;

public class ActionFieldValueEditor extends Composite {

    interface ActionFieldValueEditorBinder
            extends
            UiBinder<Widget, ActionFieldValueEditor> {

    }

    private static ActionFieldValueEditorBinder uiBinder = GWT.create(ActionFieldValueEditorBinder.class);

    private final String className;
    private final ActionFieldValue afv;
    private final List<ActionFieldValue> afvs;
    private final AsyncPackageDataModelOracle oracle;
    private final ParameterizedCommand<ActionFieldValue> onDeleteCallback;

    private final ValueEditorFactory valueEditorFactory = new ValueEditorFactory() {
        @Override
        protected Map<String, String> getCurrentValueMap() {
            if (afvs == null) {
                return Collections.emptyMap();
            }
            final Map<String, String> currentValueMap = new HashMap<String, String>();
            for (ActionFieldValue afv : afvs) {
                currentValueMap.put(afv.getFieldName(),
                                    ValueUtilities.convertNodeValue(afv.getValue()));
            }
            return currentValueMap;
        }
    };

    @UiField
    ListBox fieldListBox;

    @UiField
    FormGroup valueHolder;

    @UiField
    Button removeFieldValueButton;

    public ActionFieldValueEditor(final String className,
                                  final ActionFieldValue afv,
                                  final List<ActionFieldValue> afvs,
                                  final AsyncPackageDataModelOracle oracle,
                                  final ParameterizedCommand<ActionFieldValue> onDeleteCallback) {
        initWidget(uiBinder.createAndBindUi(this));

        this.className = className;
        this.afv = afv;
        this.afvs = afvs;
        this.oracle = oracle;
        this.onDeleteCallback = onDeleteCallback;

        initializeFieldNames(afv);
    }

    private void initializeFieldNames(final ActionFieldValue afv) {
        fieldListBox.clear();
        fieldListBox.setEnabled(false);

        oracle.getFieldCompletions(className,
                                   (modelFields) -> {
                                       fieldListBox.setEnabled(modelFields.length > 0);
                                       if (modelFields.length == 0) {
                                           fieldListBox.addItem(GuidedDecisionTreeConstants.INSTANCE.noFields());
                                           return;
                                       }

                                       //Add them to the ListBox
                                       int selectedIndex = -1;
                                       for (ModelField modelField : modelFields) {
                                           final String fieldName = modelField.getName();
                                           if (!fieldName.equals(DataType.TYPE_THIS)) {
                                               fieldListBox.addItem(fieldName);
                                               if (fieldName.equals(afv.getFieldName())) {
                                                   selectedIndex = fieldListBox.getItemCount() - 1;
                                               }
                                           }
                                       }

                                       fieldListBox.addChangeHandler((event) -> {
                                           final String fieldName = fieldListBox.getItemText(fieldListBox.getSelectedIndex());
                                           afv.setFieldName(fieldName);
                                           afv.setValue(null);
                                           initialiseValue();
                                       });

                                       fieldListBox.setSelectedIndex(selectedIndex);
                                       if (selectedIndex == -1) {
                                           fieldListBox.setSelectedIndex(0);
                                           final String fieldName = fieldListBox.getItemText(0);
                                           afv.setFieldName(fieldName);
                                       }
                                       initialiseValue();
                                   });
    }

    private void initialiseValue() {
        final Widget editor = valueEditorFactory().getValueEditor(className,
                                                                  afv.getFieldName(),
                                                                  afv,
                                                                  oracle,
                                                                  false);
        valueHolder.clear();
        if (editor != null) {
            valueHolder.add(editor);
        }
    }

    ValueEditorFactory valueEditorFactory() {
        return valueEditorFactory;
    }

    @SuppressWarnings("unused")
    @UiHandler("removeFieldValueButton")
    void onRemoveFieldValueButtonClick(final ClickEvent event) {
        onDeleteCallback.execute(afv);
    }
}
