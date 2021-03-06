package com.tuplejump.stargate.perf;

import com.tuplejump.stargate.cas.IndexTestBase;
import com.tuplejump.stargate.util.CQLUnitD;
import org.junit.Test;

/**
 * User: satya
 */
public class PerfSGTest extends IndexTestBase {
    String keyspace = "dummyks0";

    public PerfSGTest() {
        cassandraCQLUnit = CQLUnitD.getCQLUnit(null);
    }

    @Test
    public void perfTestSG() {
        try {
            createKS(keyspace);
            System.out.println("--------SG indexes------");
            createTableAndIndexForRow();
            //prime up the cache just in case
            for (int i = 0; i < states.length; i++) {
                String state = states[i];
                countResults("TAG3", "state = '" + state + "'", false);
            }
            for (int i = 0; i < states.length; i++) {
                String state = states[i];
                countResults("TAG3", "state1 = '" + state + "'", false);
            }

            System.out.println("--------Cache primed-----");
            countResults("TAG3", "state = 'NONEXISTENT'", false);
            /*for (int i = 0; i < 5; i++) {
                String state = getRandomState();
                countResults("TAG3", "state = '" + state + "'", false);
            }*/
        } finally {
            dropTable(keyspace, "TAG3");
            dropKS(keyspace);
        }
    }


    private void createTableAndIndexForRow() {
        getSession().execute("USE " + keyspace + ";");
        getSession().execute("CREATE TABLE TAG3(key int primary key, tags varchar, state varchar, state1 varchar)");
        int i = 0;
        long r5k = System.nanoTime();
        while (i < 12000) {
            long rowsTime = System.nanoTime();
            if (i == 6000) {
                getSession().execute("CREATE CUSTOM INDEX stateindex ON TAG3(state) USING 'com.tuplejump.stargate.cas.PerColIndex'");
                getSession().execute("CREATE INDEX stateindex1 ON TAG3(state1)");
                System.out.println("-------- Created Index ---------");
            }

            getSession().execute("insert into " + keyspace + ".TAG3 (key,tags,state,state1) values (" + (i + 1) + ",'hello1 tag1 lol1', 'CA','CA')");
            getSession().execute("insert into " + keyspace + ".TAG3 (key,tags,state,state1) values (" + (i + 2) + ",'hello1 tag1 lol2', 'LA','LA')");
            getSession().execute("insert into " + keyspace + ".TAG3 (key,tags,state,state1) values (" + (i + 3) + ",'hello1 tag2 lol1', 'NY','NY')");
            getSession().execute("insert into " + keyspace + ".TAG3 (key,tags,state,state1) values (" + (i + 4) + ",'hello1 tag2 lol2', 'TX','TX')");
            getSession().execute("insert into " + keyspace + ".TAG3 (key,tags,state,state1) values (" + (i + 5) + ",'hllo3 tag3 lol3' , 'TX','TX')");
            getSession().execute("insert into " + keyspace + ".TAG3 (key,tags,state,state1) values (" + (i + 6) + ",'hello2 tag1 lol1', 'CA','CA')");
            getSession().execute("insert into " + keyspace + ".TAG3 (key,tags,state,state1) values (" + (i + 7) + ",'hello2 tag1 lol2', 'NY','NY')");
            getSession().execute("insert into " + keyspace + ".TAG3 (key,tags,state,state1) values (" + (i + 8) + ",'hello2 tag2 lol1', 'CA','CA')");
            getSession().execute("insert into " + keyspace + ".TAG3 (key,tags,state,state1) values (" + (i + 9) + ",'hello2 tag2 lol2', 'TX','TX')");
            getSession().execute("insert into " + keyspace + ".TAG3 (key,tags,state,state1) values (" + (i + 10) + ",'hllo3 tag3 lol3', 'TX','TX')");
            i = i + 10;
            if (i % 3000 == 0) {
                double taken = (rowsTime - r5k) / 1000000;
                r5k = rowsTime;
                System.out.println("Time taken for 2k is -" + taken + " milli seconds");
            }
        }
    }

}
