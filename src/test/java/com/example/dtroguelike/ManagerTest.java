package com.example.dtroguelike;

import com.example.dtroguelike.application.ManagerService;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerStyle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    @Test
    void createsManagerWithGivenData() {
        ManagerService service = new ManagerService();
        Manager manager = service.createManager("Marcelo Bianchi", 45, "Argentina", ManagerStyle.STRATEGIST);

        assertEquals("Marcelo Bianchi", manager.getName());
        assertEquals(45, manager.getAge());
        assertEquals("Argentina", manager.getNationality());
        assertEquals(ManagerStyle.STRATEGIST, manager.getStyle());
    }

    @Test
    void styleAppliesInitialAttributes() {
        ManagerService service = new ManagerService();
        Manager manager = service.createManager("DT", 40, "Argentina", ManagerStyle.STRATEGIST);

        assertEquals(75, manager.getAttributes().getTactics());
        assertEquals(55, manager.getAttributes().getManagement());
    }

    @Test
    void rejectsBlankName() {
        ManagerService service = new ManagerService();
        assertThrows(IllegalArgumentException.class,
                () -> service.createManager("  ", 40, "Argentina", ManagerStyle.LEADER));
    }
}
