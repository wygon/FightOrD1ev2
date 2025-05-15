package championAssets;

public class Fighter extends Champion {

    public Fighter(Fighter f) {
        super(f.name, f.healthPoints, f.attackDamage, f.magicDamage, f.physicalResist, f.magicResist, f.distancePoint, 
                copyAbilities(f.abilities));
        this.specialSpellType = SpellType.OFF;
    }
    public Fighter(){
        
    }
    @Override
    public String toString() {
        return super.toString() + "\n Fighter";
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
        return SpellType.OFF;
    }

    @Override
    public void setSpecialSpellValue(double thornsValue) {
    }

    @Override
    public void setSpecialSpellMove(int thornsMove) {
    }

    @Override
    public void setSpecialSpellType(SpellType specialSpellType) {
    }
}
