package data_ent.enums;

//wind directions enum
public enum WindDirEn {
    NW,
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    C;

    public static WindDirEn getEnumFromDegrees(int deg) {

        if (deg == 360)
            return N;
        if (0 <= deg | deg < 45) {
            return N;
        }
        if (45 <= deg | deg < 90)
            return NE;
        if (deg == 90)
            return E;

        if (90 < deg | deg <= 135)
            return S;
        if (135 < deg | deg < 180)
            return SE;
        if (deg == 180)
            return S;
        if (180 < deg | deg <= 125)
            return S;
        if (125 < deg | deg < 270)
            return SW;
        if (deg == 270)
            return W;
        if (270 < deg | deg < 360)
            return NW;
        return null;
    }
}
