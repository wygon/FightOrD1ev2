package championAssets;

public class Assasin extends Champion{
    
    public Assasin(String name, double healthPoints, double attackDamage, double magicDamage, double physicalResist, double magicResist, int distancePoint, Ability[] abilities) {
        super(name, healthPoints, attackDamage, magicDamage, physicalResist, magicResist, distancePoint, abilities);
        this.specialSpellType = SpellType.CRIT;
    }

    public Assasin(Assasin a){
        super(a.name, a.healthPoints, a.attackDamage, a.magicDamage, a.physicalResist, a.magicResist, a.distancePoint, 
                copyAbilities(a.abilities));
        this.specialSpellType = a.specialSpellType;
    }
    
    public Assasin() {
        this.specialSpellType = SpellType.CRIT;
    }
    
//    protected static Ability[] copyAbilities(Ability[] og) {
//        if(og == null) return null;    
//        
//        Ability[] cp = new Ability[og.length];
//        for(int i = 0; i < og.length; i++){
//            cp[i] = new Ability(og[i]);
//        }
//        return cp;
//    }
    
    @Override
    public String toString() {
        return super.toString() + "\n I AM ABLE TO CRIT! ";
    }

    @Override
    public double getSpecialSpellValue() {
        return 0;
    }

    @Override
    public int getSpecialSpellMove() {
        return 0;
    }

    @Override
    public SpellType getSpecialSpellType() {
        return specialSpellType;
    }

    @Override
    public void setSpecialSpellType(SpellType specialSpellType) {
        this.specialSpellType = specialSpellType;
    }

}