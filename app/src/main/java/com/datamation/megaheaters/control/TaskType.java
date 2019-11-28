package com.datamation.megaheaters.control;

public enum TaskType

{
    DATABASENAME(1),
    FSALREP(2),
    FCONTROL(3),
    FCOMPANYSETTING(4),
    FCOMPANYBRANCH(5),
    FDEBTOR(6),
    FLOCATIONS(7),
    FITEMLOC(8),
    FITEMPRI(9),
    FITEMS(10),

     FAREA(11),
    FSTKIN(12),
//    FFREEHED(4),
//    FFREEDET(5),
//    FFREEDEB(6),
//    FITENRDET(7),
//    FITENRHED(8),
//    FITEDEBDET(9),





//    FFREESLAB(18),
    FREASON(12),
    FROUTE(13),
    FBANK(14),
    FDDBNOTE(15),
//    FEXPENSE(23),

//    FMERCH(25),
    FROUTEDET(16),
//    FTRGCAPUL(27),
//    FTYPE(28),
//    FSUBBRAND(29),
//    FGROUP(30),
//    FSKU(31),
//    FBRAND(32),
//    FDEALER(33),
//    FDISCDEB(34),
//    FDISCDET(35),
//    FDISCSLAB(36),
//    FDISCHED(37),
//    FFREEITEM(38),
//    FFREEMSLAB(39),
//    FDISCVHED(40),
//    FDISCVDET(41),
//    FDISCVDEB(42),
//    FINVHEDL3(43),
//    FINVDETL3(44),
//    FDSCHHED(45),
//    FDSCHDET(46),
//    FSIZECOMB(47),
//    FSIZEIN(48),
//    FCRCOMB(49),
//    FTERMS(50),
//    FTAX(17),
    FTAXHED(17),
    FTAXDET(18),
    FEXPENSE(19),
    //    FTOWN(24),
//    FTOURHED(54),
//    FDEBITEMPRI(55),

//    FBRANDTARGET(57),

   // UPLOADLOADING(20),
    UPLOADTOUR(21),
    UPLOADVANSALES(20),
 //   UPLOAD_EXPENSE(23),

    UPLOADRETURN(24),
    //FDISTRICT(13),
 //   UPLOADNEWCUSTOMER(26),
    UPLOADRECEIPT(22),
    UPLOAD_NONPROD(23),
    UPLOAD_EXPENSE(25),
    UPLOAD_RETURNS(26);

    int value;

    private TaskType(int value) {
        this.value = value;
    }
}
