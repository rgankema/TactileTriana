/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.house;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Richard
 */
public class HouseVMTest {
    
    private House mockedHouse;
    private DoubleProperty houseCurrentConsumption;
    private DoubleProperty houseMaxConsumption;
    private BooleanProperty fuseBlown;
    
    @Before
    public void setUp() {
        /* Mock het huis en geef hem standaard 'antwoord'. */
        mockedHouse = mock(House.class);
        
        houseCurrentConsumption = new SimpleDoubleProperty(10);
        houseMaxConsumption = new SimpleDoubleProperty(100);
        fuseBlown = new SimpleBooleanProperty(false);
        
        when(mockedHouse.currentConsumptionProperty()).thenReturn(houseCurrentConsumption);
        when(mockedHouse.maximumConsumptionProperty()).thenReturn(houseMaxConsumption);
        when(mockedHouse.fuseBlownProperty()).thenReturn(fuseBlown);
    }

    // LOAD PROPERTY
    
    @Test
    public void testLoadProperty_PositiveConsumption() {
        HouseVM instance = new HouseVM(mockedHouse);
        
        houseCurrentConsumption.set(5);
        houseMaxConsumption.set(10);
        
        assertEquals(0.5, instance.getLoad(), 0.01);
    }
    
    @Test
    public void testLoadProperty_NegativeConsumption() {
        HouseVM instance = new HouseVM(mockedHouse);
        
        houseCurrentConsumption.set(-5);
        houseMaxConsumption.set(10);
        
        assertEquals(0.5, instance.getLoad(), 0.01);
    }
    
    @Test
    public void testLoadProperty_ConsumptionOverMax_CappedAt1() {
        HouseVM instance = new HouseVM(mockedHouse);
        
        houseCurrentConsumption.set(15);
        houseMaxConsumption.set(10);
        
        assertEquals(1.0, instance.getLoad(), 0.01);
    }
    
    @Test
    public void testLoadProperty_NoConsumption_LoadAt0() {
        HouseVM instance = new HouseVM(mockedHouse);
        
        houseCurrentConsumption.set(0);
        houseMaxConsumption.set(10);
        
        assertEquals(0.0, instance.getLoad(), 0.01);
    }

    // FUSE BLOWN
    
    @Test
    public void testFuseBlownProperty_ModelFuseBlownTrue_VMFuseBlownTrue() {
        HouseVM instance = new HouseVM(mockedHouse);
        
        fuseBlown.set(true);
        
        assertEquals(true, instance.isFuseBlown());
    }
    
    @Test
    public void testFuseBlownProperty_ModelFuseBlownFalse_VMFuseBlownFalse() {
        HouseVM instance = new HouseVM(mockedHouse);
        
        fuseBlown.set(false);
        
        assertEquals(false, instance.isFuseBlown());
    }

    // REPAIR FUSE
    
    @Test
    public void testRepairFuse_CallsModelRepairFuse() {
        HouseVM instance = new HouseVM(mockedHouse);
        
        instance.pressed();
        
        verify(mockedHouse).repairFuse();
    }
    
}
