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
package client.scenes;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MainCtrlTest {

    private MainCtrl sut;
    private Stage stage = mock(Stage.class);
    private Pair overview = mock(Pair.class);
    private Pair startScreen = mock(Pair.class);
    private Pair addQuote = mock(Pair.class);
    private Scene aqs = mock(Scene.class);
    private Scene sss = mock(Scene.class);
    private Scene qos = mock(Scene.class);
    private AddQuoteCtrl aqc = mock(AddQuoteCtrl.class);
    private StartScreenCtrl ssc = mock(StartScreenCtrl.class);
    private QuoteOverviewCtrl qoc = mock(QuoteOverviewCtrl.class);

    @BeforeEach
    public void setup() {
        sut = new MainCtrl();
        when(overview.getKey()).thenReturn(qoc);
        when(overview.getValue()).thenReturn(qos);
        when(startScreen.getKey()).thenReturn(ssc);
        when(startScreen.getValue()).thenReturn(sss);
        when(addQuote.getKey()).thenReturn(aqc);
        when(addQuote.getValue()).thenReturn(aqs);
        sut.initialize(stage, overview, addQuote, startScreen);
    }

    @Test
    void initializeTest() {
        assertEquals(stage, sut.getPrimaryStage());
        assertEquals(qoc, sut.getOverviewCtrl());
        assertEquals(qos, sut.getOverview());
        assertEquals(aqc, sut.getAddCtrl());
        assertEquals(aqs, sut.getAdd());
        assertEquals(ssc, sut.getStartScreenCtrl());
        assertEquals(sss, sut.getStartScreen());
        verify(stage).show();
    }

    @Test
    void showOverviewTest() {
        sut.showOverview();
        verify(stage).setTitle("Quotes: Overview");
        verify(stage).setScene(qos);
        verify(qoc).refresh();
    }

    @Test
    void showStartMenuTest() {
        sut.showStartMenu();
        verify(stage, times(2)).setTitle("Splitty: Start Screen");
        verify(stage, times(2)).setScene(sss);
    }

    @Test
    void showAddTest() {
        sut.showAdd();
        verify(stage).setTitle("Quotes: Adding Quote");
        verify(stage).setScene(aqs);
        verify(aqs).setOnKeyPressed(any());
    }
}