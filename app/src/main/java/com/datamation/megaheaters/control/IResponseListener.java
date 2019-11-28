package com.datamation.megaheaters.control;

public interface IResponseListener {

    void moveNextFragment_Van();

    void moveNextFragment_Pre();

    void moveNextFragment_NonProd();

    void whenDiscardClicked_Ret();

    void whenSaveClicked_Ret();

    void moveNextFragment_Ret();

    void whenPauseClicked_Ret();

    void moveNextFragmentRece();

    void moveToDetailsRece();

}
