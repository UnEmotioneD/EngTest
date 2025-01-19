package kr.or.iei.model.vo;

public class WordWithIndex extends Word {
    private int index;

    public WordWithIndex(String word, String def1, String def2, int index) {
        super(word, def1, def2);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return index + "\t" + super.toString();
    }

}
