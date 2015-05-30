/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.utwente.ewi.caes.tactiletriana.Concurrent;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.chart.ChartVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.datetime.DateTimeVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.notification.NotificationVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.weather.WeatherVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.LoggingEntityVMBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntityBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.TimeScenario.TimeSpan;

/**
 *
 * @author Richard
 */
public class DetailVM {

    private final Simulation simulation;
    private final DateTimeVM dateTimeVM;
    private final WeatherVM weatherVM;
    private final ChartVM chartVM;
    private final ChartVM[] subChartVMs;
    private final LoggingEntityVMBase[] loggersOnCharts;
    int subChartIndex = 0;
    
    private final ObservableList<NotificationVM> notificationQueue;
    private final ObservableList<NotificationVM> notificationQueueUnmodifiable;
    
    public DetailVM(Simulation simulation) {
        this.simulation = simulation;

        dateTimeVM = new DateTimeVM(simulation);
        chartVM = new ChartVM();
        weatherVM = new WeatherVM(simulation);
        subChartVMs = new ChartVM[3];
        for (int i = 0; i < 3; i++) {
            subChartVMs[i] = new ChartVM();
        }
        loggersOnCharts = new LoggingEntityVMBase[3];
        
        notificationQueue = FXCollections.observableArrayList();
        notificationQueueUnmodifiable = FXCollections.unmodifiableObservableList(notificationQueue);
    }
    
    // PROPERTIES
    
    /**
     * An unmodifiable observable list of notifications that are to be shown. To
     * add a notification to the queue, call {@link notifiy}. The notifications
     * will be in the list for as long as the VM deems they should be shown.
     * 
     * @return the list of pending notifications
     */
    public ObservableList<NotificationVM> getNotificationQueue() {
        return notificationQueueUnmodifiable;
    }
    
    // METHODS
    
    /**
     * Shows a pop-up notification on the screen with the given message.
     * 
     * @param message the notification message
     */
    public void showNotification(String message) {
        NotificationVM notification = new NotificationVM(message);
        notificationQueue.add(notification);
        // Remove the notification after 5000ms
        Concurrent.getExecutorService().schedule(() -> { 
            notificationQueue.remove(notification);
        }, 5000, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Shows a LoggingEntityBase on a chart.
     * 
     * @param loggerVM the VM for the LoggingEntityBase
     * @param actual the actual LoggingEntityBase to be shown on a chart
     * @param future the future LoggingEntityBase to be shown on a chart
     */
    public void showOnChart(LoggingEntityVMBase loggerVM, LoggingEntityBase actual, LoggingEntityBase future) {
        loggerVM.setShownOnChart(true);
        loggerVM.setChartIndex(subChartIndex);
        
        LoggingEntityVMBase old = loggersOnCharts[subChartIndex];
        loggersOnCharts[subChartIndex] = loggerVM;
        subChartVMs[subChartIndex].setEntity(actual, future);
        subChartIndex++;
        subChartIndex = (subChartIndex == subChartVMs.length) ? 0 : subChartIndex;
        
        if (old != null) old.setShownOnChart(false);
    }
    
    // CALLBACKS FOR VIEW
    
    /**
     * To be called by the view when it gets coupled to this VM. Describes a function
     * that should be called when the Simulation jumps to a new TimeSpan.
     * 
     * @param callback the function that should be called
     */
    public void setOnSimulationTimeSpanChange(Consumer<TimeSpan> callback) {
        this.simulation.getTimeScenario().addNewTimeSpanStartedCallback(callback);
        this.simulation.timeScenarioProperty().addListener((obs, oV, nV) -> { 
            oV.removeNewTimeSpanStartedCallback(callback);
            nV.addNewTimeSpanStartedCallback(callback);
        });
    }

    // Child VMs
    
    public DateTimeVM getDateTimeVM() {
        return dateTimeVM;
    }

    public ChartVM getChartVM() {
        return chartVM;
    }

    public WeatherVM getWeatherVM() {
        return weatherVM;
    }
    
    public ChartVM getSubChartVM(int index) {
        return subChartVMs[index];
    }
}
