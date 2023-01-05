package ar.com.gt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ar.com.gt.utad.DataType;
import ar.com.gt.utad.TransferibleApplicationData;
import lombok.extern.java.Log;

/**
 * Unit test for simple App.
 */
@Log
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {

        TransferibleApplicationData tad = new TransferibleApplicationData();

        tad.defineVariable("help", DataType.BOOLEAN, false, null, new String[] { "h" }, new String[] { "CLI" });
        tad.setValue("Prueba", 1);
        tad.setValue("OtraPrueba", 2);
        tad.setValue("Prueba", 3);

        tad.setValue("ByteAValue", "algo que sea bytea".getBytes());

        tad.setValue("PruebaArray", 2, new String[] { "TAG1", "TAG2" }, 1);
        tad.setValue("PruebaArray", 1, 0);
        tad.setValue("PruebaArray", 3, 2);
        tad.setValue("PruebaArray", 8, 7);

        tad.setValue("PruebaArray2D", "1,1", 0, 0);
        tad.setValue("PruebaArray2D", "3,2", 2, 1);
        tad.setValue("PruebaArray2D", "2,3", 1, 2);

        tad.setValueFromString("Parametro[1][2]=algo");

        String json = TransferibleApplicationData.getJson(tad);

        log.info(json);

        TransferibleApplicationData tmp = TransferibleApplicationData.fromJson(json);

        assertFalse(tmp.findVarDefinition("h").haveValue());
        assertTrue(tmp.findVarDefinition("ByteaVALUE").haveValue());
        assertTrue(new String(tmp.findVarDefinition("ByteaVALUE").getValue().getByteArrayValue())
                .equalsIgnoreCase("algo que sea bytea"));

        assertTrue(true);
    }
}
