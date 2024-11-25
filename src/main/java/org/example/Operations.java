package org.example;

import java.util.List;

public interface Operations {
    void addNewUser() throws ExitException;
    void addNewBook() throws ExitException;
    void addNewLend() throws ExitException;
    void returnBook() throws ExitException;

    void printLendByYear() throws ExitException;

    void changeUserByLend(List<Lend> lendList) throws ExitException;

    void printBookByUserLend() throws ExitException;
}