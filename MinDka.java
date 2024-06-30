package utr_lab2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class MinDka {
	public static void main(String[] args) throws FileNotFoundException {

		String dka[][] = null;
		ArrayList<String> stanja = null;
		ArrayList<String> abeceda = null;
		ArrayList<String> prihvatljivaStanja = new ArrayList<String>();
		String pocetnoStanje = null;

		File file = new File("src/lab2_primjeri[1]/test02/t.ul");
		Scanner sc = new Scanner(System.in);

		int i = 0;
		while (sc.hasNext()) {
			i++;
			String line = sc.nextLine();
			switch (i) {
			case 1:
				stanja = new ArrayList<>(Arrays.asList(line.split(",")));
				continue;
			case 2:
				abeceda = new ArrayList<>(Arrays.asList(line.split(",")));
				dka = new String[stanja.size()][abeceda.size()];
				continue;
			case 3:
				if (prihvatljivaStanja != null) prihvatljivaStanja = new ArrayList<>(Arrays.asList(line.split(",")));
				continue;
			case 4:
				pocetnoStanje = line;
				continue;
			default:
				String parsedString = line.replaceAll(",", " ").replaceAll("->", " ").replaceAll("\\s+", " ");
				String[] splitString = parsedString.split(" ");
				String trenutnoStanje = splitString[0];
				String simbolAbecede = splitString[1];
				String iduceStanje = splitString[2];

				dka[stanja.indexOf(trenutnoStanje)][abeceda.indexOf(simbolAbecede)] = iduceStanje;
			}
		}
		sc.close();

//		System.out.println(" " + abeceda);
//		for (int j = 0; j < stanja.size(); j++) {
//			for (int k = 0; k < abeceda.size(); k++) {
//				sysout
//				System.out.print(dka[j][k] + " ");
//			}
//			System.out.println();
//		}

		// micanje nedohvatljivih stanja
		LinkedHashSet<String> dohvatljivaStanja = new LinkedHashSet<String>();
		dohvatljivaStanja.add(pocetnoStanje);

		while (true) {
			LinkedHashSet<String> novaStanja = new LinkedHashSet<String>();
			for (String stanje : dohvatljivaStanja) {
				for (int i1 = 0; i1 < abeceda.size(); i1++) {
					String novoStanje = dka[stanja.indexOf(stanje)][i1];
					if (novoStanje != null) {
						novaStanja.add(novoStanje);
					}
				}
			}
			int prevSize = dohvatljivaStanja.size();
			dohvatljivaStanja.addAll(novaStanja);
			int newSize = dohvatljivaStanja.size();
			if (prevSize == newSize)
				break;
		}
		//System.out.println(dohvatljivaStanja);
		

		// micanje istovjetnih stanja
		ArrayList<String> neprihvatljivaStanja = new ArrayList<>();
		neprihvatljivaStanja.addAll(stanja);
		neprihvatljivaStanja.removeAll(prihvatljivaStanja);

		LinkedHashSet<TreeSet<String>> P = new LinkedHashSet<>();
		P.add(new TreeSet<String>(prihvatljivaStanja));
		P.add(new TreeSet<String>(neprihvatljivaStanja));

		Set<Set<String>> W = new LinkedHashSet<>();
		W.add(new LinkedHashSet<String>(prihvatljivaStanja));
		W.add(new LinkedHashSet<String>(neprihvatljivaStanja));

		LinkedHashSet<TreeSet<String>> PCopy = new LinkedHashSet<>();
		PCopy.addAll(P);

		while (!W.isEmpty()) {
			Iterator<Set<String>> iterator = W.iterator();
			Set<String> A = iterator.next();
			iterator.remove();

			for (String znak : abeceda) {
				TreeSet<String> X = new TreeSet<>();
				for (String stanje : stanja) {
					if (!dohvatljivaStanja.contains(stanje)) continue;
					String novoStanje = dka[stanja.indexOf(stanje)][abeceda.indexOf(znak)];
					if (A.contains(novoStanje))
						X.add(stanje);
				}
				for (TreeSet<String> Y : PCopy) {
					TreeSet<String> XintersectY = new TreeSet<String>();
					XintersectY.addAll(X);
					XintersectY.retainAll(Y);
					TreeSet<String> YwithoutX = new TreeSet<String>();
					YwithoutX.addAll(Y);
					YwithoutX.removeAll(X);
					if ((!XintersectY.isEmpty()) && (!YwithoutX.isEmpty())) {
						P.remove(Y);
						P.add(XintersectY);
						P.add(YwithoutX);

						if (W.contains(Y)) {
							W.remove(Y);
							W.add(XintersectY);
							W.add(YwithoutX);
						}
						else {
							if (XintersectY.size() <= YwithoutX.size())
								W.add(XintersectY);
							else
								W.add(YwithoutX);
						}
					}
				}
				PCopy.clear();
				PCopy.addAll(P);
			}
		}
		
		Iterator<TreeSet<String>> iter = P.iterator();
		while(iter.hasNext()) {
			Set<String> set = iter.next();
			if (set.isEmpty()) iter.remove();
			if (set.contains("") && set.size() == 1) iter.remove();
		}
		//System.out.println(P);
		
		// updejtanje DKA
		for (int n = 0; n < stanja.size(); n++) {
			for (int m = 0; m < abeceda.size(); m++) {
				for (TreeSet<String> setStanja : P) {
					if (setStanja.contains(dka[n][m])) {
						Iterator<String> iterator = setStanja.iterator();
						dka[n][m] = iterator.next();
						break;
					}
				}
			}
		}
		
		// formatiranje ispisa i printanje :D
		
		// Stanja
		TreeSet<String> novaStanja = new TreeSet<String>();
		for (TreeSet<String> set : P) {
			if (set.size() == 1) {
				novaStanja.addAll(set);
				continue;
			}
			else {
				Iterator<String> iterator = set.iterator();
				if (!set.isEmpty()) {
					String s = iterator.next();
					novaStanja.add(s);
				}
			}
		}
		novaStanja.retainAll(dohvatljivaStanja);
		String stringStanja = String.join(",", novaStanja);
		System.out.println(stringStanja);
		
		//Abeceda
		TreeSet<String> setAbeceda = new TreeSet<String>(abeceda);
		String stringAbeceda = String.join(",", setAbeceda);
		System.out.println(stringAbeceda);
		
		// Prihvatljiva stanja
		TreeSet<String> novaPrihvatljivaStanja = new TreeSet<>();
		novaPrihvatljivaStanja.addAll(prihvatljivaStanja);
		novaPrihvatljivaStanja.retainAll(novaStanja);
		String novaPrihvatljivaStanjaString = String.join(",", novaPrihvatljivaStanja);
		System.out.println(novaPrihvatljivaStanjaString);
		
		// Pocetno stanje
		String novoPocetnoStanjeString = null;
		for (TreeSet<String> set1 : P) {
			if (set1.contains(pocetnoStanje)) {
				Iterator<String> iterator = set1.iterator();
				novoPocetnoStanjeString = iterator.next();
				break;
			}
		}
		System.out.println(novoPocetnoStanjeString);
		
		// Funkcije prijelaza
		for (String strStanje : novaStanja) {
			for (String strAbeceda : setAbeceda) {
				String trenutnoStanje = strStanje;
				String simbolPrijelaza = strAbeceda;
				String sljedeceStanje = dka[stanja.indexOf(strStanje)][abeceda.indexOf(strAbeceda)];
				String prijelazString = trenutnoStanje + "," + simbolPrijelaza + "->" + sljedeceStanje;
				System.out.println(prijelazString);
			}
		}
		
	}
} 
