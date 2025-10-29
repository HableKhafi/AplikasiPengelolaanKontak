/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import model.Kontak;
import model.KontakDAO;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class KontakController {
    
    private KontakDAO contactDAO;

    public KontakController() {
        contactDAO = new KontakDAO();
    }

    public List<Kontak> getAllContacts() throws SQLException {
        return contactDAO.getAllContacts();
    }

    public void addContact(String nama, String nomorTelepon, String kategori) throws SQLException {
        Kontak contact = new Kontak(0, nama, nomorTelepon, kategori);
        contactDAO.addContact(contact);
    }

    public void updateContact(int id, String nama, String nomorTelepon, String kategori) throws SQLException {
        Kontak contact = new Kontak(id, nama, nomorTelepon, kategori);
        contactDAO.updateContact(contact);
    }

    public void deleteContact(int id) throws SQLException {
        contactDAO.deleteContact(id);
    }

    public List<Kontak> searchContacts(String keyword) throws SQLException {
        return contactDAO.searchContacts(keyword);
    }

    public boolean isDuplicatePhoneNumber(String nomorTelepon, Integer excludeId) throws SQLException {
        return contactDAO.isDuplicatePhoneNumber(nomorTelepon, excludeId);
    }
    
}
