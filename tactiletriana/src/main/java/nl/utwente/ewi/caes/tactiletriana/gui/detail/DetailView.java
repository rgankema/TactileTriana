/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail;

import java.util.HashMap;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.chart.ChartView;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.datetime.DateTimeView;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.notification.NotificationVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.notification.NotificationView;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.weather.WeatherView;

/**
 *
 * @author Richard
 */
public class DetailView extends BorderPane {

    @FXML
    private StackPane header;
    @FXML
    private DateTimeView dateTimeView;
    @FXML
    private ChartView mainChart;
    @FXML
    private ChartView subChart1;
    @FXML
    private ChartView subChart2;
    @FXML
    private ChartView subChart3;
    @FXML
    private WeatherView weatherView;
    @FXML
    private ImageView trianaLogo;

    private final HashMap<NotificationVM, NotificationView> notificationViewByVM;

    private DetailVM viewModel;

    public DetailView() {
        ViewLoader.load(this);

        notificationViewByVM = new HashMap<>();
    }

    public void setViewModel(DetailVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel can only be set once");
        }

        this.viewModel = viewModel;

        dateTimeView.setViewModel(viewModel.getDateTimeVM());
        mainChart.setViewModel(viewModel.getChartVM());
        weatherView.setViewModel(viewModel.getWeatherVM());

        subChart1.setViewModel(viewModel.getSubChartVM(0));
        subChart2.setViewModel(viewModel.getSubChartVM(1));
        subChart3.setViewModel(viewModel.getSubChartVM(2));

        // Fade in and out when a the simulation shifts to a new timespan
        final FadeTransition fadeOut = new FadeTransition(Duration.millis(200), mainChart);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        final FadeTransition fadeIn = new FadeTransition(Duration.millis(300), mainChart);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        final SequentialTransition transition = new SequentialTransition(fadeOut, fadeIn);
        viewModel.setOnSimulationTimeSpanChange(() -> transition.playFromStart());

        // Show notifications on the screen
        viewModel.getNotificationQueue().addListener((ListChangeListener.Change<? extends NotificationVM> c) -> {
            while (c.next()) {
                for (NotificationVM nVM : c.getAddedSubList()) {
                    popup(nVM);
                }
                for (NotificationVM nVM : c.getRemoved()) {
                    remove(nVM);
                }
            }
        });
    }

    private void popup(NotificationVM notificationVM) {
        NotificationView notificationView = new NotificationView();
        StackPane.setAlignment(notificationView, Pos.CENTER_RIGHT);
        notificationView.setViewModel(notificationVM);
        notificationViewByVM.put(notificationVM, notificationView);

        header.getChildren().add(notificationView);
        final FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notificationView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.playFromStart();
    }

    private void remove(NotificationVM notificationVM) {
        NotificationView notificationView = notificationViewByVM.remove(notificationVM);

        final FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notificationView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(x -> header.getChildren().remove(notificationView));
        fadeOut.playFromStart();
    }
}
