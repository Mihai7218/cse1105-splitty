/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import client.scenes.*;
import client.utils.*;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Modality;


public class MyModule implements Module {

    /**
     *
     * @param binder
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(Config.class).in(Scopes.SINGLETON);
        binder.bind(ServerUtils.class).in(Scopes.SINGLETON);
        binder.bind(MailSender.class).in(Scopes.SINGLETON);
        binder.bind(MainCtrl.class).in(Scopes.SINGLETON);
        binder.bind(StartScreenCtrl.class).in(Scopes.SINGLETON);
        binder.bind(LanguageManager.class).in(Scopes.SINGLETON);
        binder.bind(CurrencyConverter.class).in(Scopes.SINGLETON);
        binder.bind(ParticipantCtrl.class).in(Scopes.SINGLETON);
        binder.bind(OverviewCtrl.class).in(Scopes.SINGLETON);
        binder.bind(EditParticipantCtrl.class).in(Scopes.SINGLETON);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        binder.bind(Alert.class).toInstance(alert);
    }
}