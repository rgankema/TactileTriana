/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.cable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.CableBase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Richard
 */
public class CableVMTest {
    private CableBase mockedCable;
    private DoubleProperty cableCurrent;
    private DoubleProperty cableMaxCurrent;
    private BooleanProperty cableBroken;
    
    public CableVMTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        mockedCable = mock(CableBase.class);
        cableCurrent = new SimpleDoubleProperty();
        cableMaxCurrent = new SimpleDoubleProperty();
        cableBroken = new SimpleBooleanProperty();
        when(mockedCable.currentProperty()).thenReturn(cableCurrent);
        when(mockedCable.maximumCurrentProperty()).thenReturn(cableMaxCurrent);
        when(mockedCable.brokenProperty()).thenReturn(cableBroken);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of loadProperty method, of class CableVM.
     */
    @Test
    public void testLoadPropertyPositiveCurrent() {
        System.out.println("loadPropertyPositiveCurrent");
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(2);
        cableMaxCurrent.set(10);
        
        assertEquals(0.2, instance.getLoad(), 0.01);
    }
    
    /**
     * Test of loadProperty method, of class CableVM.
     */
    @Test
    public void testLoadPropertyNegativeCurrent() {
        System.out.println("loadPropertyNegativeCurrent");
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(-5);
        cableMaxCurrent.set(10);
        
        assertEquals(0.5, instance.getLoad(), 0.01);
    }
    
    /**
     * Test of loadProperty method, of class CableVM.
     */
    @Test
    public void testLoadPropertyMoreThanMaxCurrent() {
        System.out.println("loadPropertyMoreThanMaxCurrent");
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(15);
        cableMaxCurrent.set(10);
        
        assertEquals(1.0, instance.getLoad(), 0.01);
    }

    /**
     * Test of directionProperty method, of class CableVM.
     */
    @Test
    public void testDirectionPropertyPositiveCurrent() {
        System.out.println("directionPropertyPostiveCurrent");
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(5);
        
        assertEquals(CableVM.Direction.END, instance.getDirection());
    }
    
    /**
     * Test of directionProperty method, of class CableVM.
     */
    @Test
    public void testDirectionPropertyNegativeCurrent() {
        System.out.println("directionPropertyNegativeCurrent");
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(-5);
        
        assertEquals(CableVM.Direction.START, instance.getDirection());
    }
    
    /**
     * Test of directionProperty method, of class CableVM.
     */
    @Test
    public void testDirectionPropertyNoCurrent() {
        System.out.println("directionPropertyNoCurrent");
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(0);
        
        assertEquals(CableVM.Direction.NONE, instance.getDirection());
    }
    
    /**
     * Test of directionProperty method, of class CableVM.
     */
    @Test
    public void testDirectionPropertyBroken() {
        System.out.println("directionPropertyNoCurrent");
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(5);
        cableBroken.set(true);
        
        assertEquals(CableVM.Direction.NONE, instance.getDirection());
    }
    
}
