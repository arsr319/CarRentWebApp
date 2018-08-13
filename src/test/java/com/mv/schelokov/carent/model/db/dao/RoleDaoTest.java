package com.mv.schelokov.carent.model.db.dao;

import com.mv.schelokov.carent.model.db.dao.exceptions.DbException;
import com.mv.schelokov.carent.model.entity.Role;
import com.mv.schelokov.carent.model.entity.builders.RoleBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Maxim Chshelokov <schelokov.mv@gmail.com>
 */
public class RoleDaoTest {
    
    private Connection connection;
    private RoleDao rr;
    
    public RoleDaoTest() {
    }
    
    @Before
    public void setUp() throws SQLException, ClassNotFoundException, 
            InstantiationException, IllegalAccessException {
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost/car_rent_test?autoReconnect=true"
                        + "&useSSL=false&characterEncoding=utf-8",
                "car_rent_app", "Un3L41NoewVA");
        rr = new RoleDao(connection);
    }
    
    @After
    public void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    public void createNewRole() throws DbException {
        assertTrue(rr.add(new RoleBuilder()
                .setRoleName("guest")
                .getRole()));
    }
    
    @Test
    public void findAllAndDeleteLast() throws DbException {
        List<Role> rl = rr.read(RoleDao.SELECT_ALL_CRITERIA);
        assertTrue(rr.remove(rl.get(rl.size()-1)));
    }
    
    @Test
    public void findAllAndUpdateSecond() throws DbException {
        Role role = rr.read(RoleDao.SELECT_ALL_CRITERIA).get(1);
        role.setRoleName("guest");
        assertTrue(rr.update(role));
    }
    
}