package kr.or.iei.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import kr.or.iei.model.vo.EnglishInterface;
import kr.or.iei.model.vo.Word;
import kr.or.iei.model.vo.WordWithIndex;
import kr.or.iei.viewer.EnglishViewer;

public class EnglishController implements EnglishInterface {
  ArrayList<Word> list;
  ArrayList<Word> searchList;
  ArrayList<Word> failList;
  ArrayList<Word> testList;
  ArrayList<WordWithIndex> continueSearchList;
  ArrayList<WordWithIndex> wordsWithIndex;
  Scanner sc;
  EnglishViewer view;

  public EnglishController() {
    list = new ArrayList<>();
    searchList = new ArrayList<>();
    continueSearchList = new ArrayList<>();
    wordsWithIndex = new ArrayList<>();
    sc = new Scanner(System.in);
    view = new EnglishViewer();
  }

  @Override
  public void mainMethod() {
    while (true) {
      importWord();
      int menu = view.menu();

      switch (menu) {
        case 1:
          search();
          break;
        case 2:
          add();
          break;
        case 3:
          test();
          break;
        case 4:
          reTest();
          break;
        case 5:
          edit();
          break;
        case 0:
          System.out.println("Terminated");
          return;
        default:
          break;
      }
    }
  }

  public void importWord() {
    BufferedReader br = null;
    try {
      FileReader fr = new FileReader("allDB.txt");
      br = new BufferedReader(fr);

      String newWord;
      while ((newWord = br.readLine()) != null) {
        String[] wordStrArr = newWord.split("/");

        list.add(new Word(wordStrArr[0], wordStrArr[1], wordStrArr[2]));
      }

    } catch (FileNotFoundException e1) {
      System.out.println("File not found");
    } catch (IOException e) {
      System.out.println("I/O Error");
    } finally {
      try {
        assert br != null;
        br.close();
      } catch (IOException e3) {
        System.out.println("br.close() I/O Error");
      }
    }
  }

  public void importFailWord() {
    BufferedReader br = null;
    try {
      FileReader fr = new FileReader("failDB.txt");
      br = new BufferedReader(fr);

      String newWord;
      while ((newWord = br.readLine()) != null) {
        String[] WordStrArr = newWord.split("/");

        failList.add(new Word(WordStrArr[0], WordStrArr[1], WordStrArr[2]));
      }
    } catch (FileNotFoundException e1) {
      System.out.println("File not found");
    } catch (IOException e) {
      System.out.println("I/O Error");
    } finally {
      try {
        assert br != null;
        br.close();
      } catch (IOException e3) {
        System.out.println("br.close() I/O Error");
      }
    }
  }

  @Override
  public void search() {
    boolean found = false;
    String searchWord;
    while (!found) {
      searchWord = view.searchViewer();
      // Cancel when typed "c"
      if (searchWord.equalsIgnoreCase("c")) {
        System.out.println("Canceling Search");
        break;
      }

      int chosenIndex = -1;
      switch (searchWord.length()) {
        case 1:
          chosenIndex = view.searchView(oneChar(searchWord));
          break;
        case 2:
          chosenIndex = view.searchView(twoChar(searchWord.charAt(0), searchWord.charAt(1)));
          break;
        case 3:
          chosenIndex =
              view.searchView(
                  threeChar(searchWord.charAt(0), searchWord.charAt(1), searchWord.charAt(2)));
          break;
        default:
          for (Word word : list) {
            if (word.getWord().equalsIgnoreCase(searchWord)
                || word.getDef1().equalsIgnoreCase(searchWord)
                || word.getDef2().equalsIgnoreCase(searchWord)) {
              System.out.println(word);
              found = true;
            } else {
              System.out.println("No such word");
            }
            break;
          }
      } // switch
      if (chosenIndex != -1) {
        view.showChosenIndex(chosenIndex - 1, wordsWithIndex);
      }
    }
  }

  public ArrayList<WordWithIndex> oneChar(String searchWord) {
    int j = 0;
    for (Word word : list) {
      if (word.getWord().charAt(0) == searchWord.charAt(0)) {
        j++;
        wordsWithIndex.add(new WordWithIndex(word.getWord(), word.getDef1(), word.getDef2(), j));
      }
    }
    return wordsWithIndex;
  }

  public ArrayList<WordWithIndex> twoChar(char firstChar, char secondChar) {
    wordsWithIndex.clear();
    int j = 0;
    for (Word word : list) {
      for (int i = 0; i < word.getWord().length() - 1; i++) {
        if (word.getWord().charAt(i) == firstChar && word.getWord().charAt(i + 1) == secondChar) {
          j++;
          wordsWithIndex.add(new WordWithIndex(word.getWord(), word.getDef1(), word.getDef2(), j));
        }
      }
    }
    return wordsWithIndex;
  }

  public ArrayList<WordWithIndex> threeChar(char firstChar, char secondChar, char thirdChar) {
    wordsWithIndex.clear();
    int j = 0;
    for (Word word : list) {
      for (int i = 0; i < word.getWord().length() - 2; i++) {
        if (word.getWord().charAt(i) == firstChar
            && word.getWord().charAt(i + 1) == secondChar
            && word.getWord().charAt(i + 2) == thirdChar) {
          j++;
          wordsWithIndex.add(new WordWithIndex(word.getWord(), word.getDef1(), word.getDef2(), j));
        }
      }
    }
    return wordsWithIndex;
  }

  @Override
  public void add() {
    BufferedWriter bw = null;
    try {
      bw = new BufferedWriter(new FileWriter("allDB.txt", true));

      bw.newLine();
      bw.write(view.addViewer("word") + "/");
      bw.write(view.addViewer("definition (1/2)") + "/");
      bw.write(view.addViewer("definition (2/2)"));

      System.out.println("New word and definitions successfully added");

    } catch (IOException e) {
      System.out.println("I/O Error");
    } finally {
      try {
        assert bw != null;
        bw.close();
      } catch (IOException e) {
        System.out.println("I/O Error");
      }
    }
  }

  @Override
  public void test() {
    System.out.println(failList);

    Random random = new Random();
    String selWord = view.startTest();
    int ranNum = view.random();
    int[] ran = new int[ranNum];

    for (int i = 0; i < ran.length; i++) {
      ran[i] = random.nextInt(list.size());
      for (int j = 0; j < i; j++) {
        if (ran[i] == ran[j]) {
          i--;
        }
      }
    }

    for (int i = 0; i < ranNum; i++) {
      if (selWord.equals("e")) {
        System.out.println(list.get(ran[i]).getDef1() + "\t" + list.get(ran[i]).getDef2());
        String answer = view.randomTest();

        if (!answer.equals(list.get(ran[i]).getWord())) {
          testList.add(list.get(ran[i]));
        }

      } else if (selWord.equals("k")) {
        System.out.println(list.get(ran[i]).getWord());
        String answer = view.randomTest();

        if (!answer.equals(list.get(ran[i]).getDef1())
            || !answer.equals(list.get(ran[i]).getDef2())) {
          testList.add(list.get(ran[i]));
        }
      } else {
        System.out.println("Error");
      }
    }
    BufferedWriter bw = null;

    try {
      FileWriter fw = new FileWriter("failDB.txt", true);
      bw = new BufferedWriter(fw);

      for (int i = 0; i < testList.size(); i++) {
        for (int j = 0; j < failList.size(); j++) {
          if (!failList.get(i).getWord().equals(testList.get(i).getWord())) {
            bw.write(testList.get(i).getWord() + "/");
            bw.write(testList.get(i).getDef1() + "/");
            bw.write(testList.get(i).getDef2());
            bw.newLine();
          }
        }
      }
      failList.clear();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void reTest() {}

  @Override
  public void edit() {
    String editWord = view.editViewer();
    boolean found = false;

    if (editWord.equalsIgnoreCase("c")) {
      System.out.println("Canceling Search");
    } else if (editWord.equalsIgnoreCase("a")) {
      System.out.println("Are you sure you want to delete all?");
      System.out.println("y / s");
      char yesOrNo = sc.next().charAt(0);

      if (yesOrNo == 'y') {

      } else {
        System.out.println("Canceling it ...");
      }

    } else {
      for (Word word : list) {
        if (word.getWord().equalsIgnoreCase(editWord)) {
          System.out.println(word);
          found = true;
          break;
        }
      }

      if (!found) {
        System.out.println("No such word");
      } else {
        //                char editOrDelete = view.editOrDelete();
      }
    }
  }
}
