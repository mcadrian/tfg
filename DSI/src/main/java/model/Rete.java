package model;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

public class Rete {
	// load up the knowledge base
	private KieServices ks = KieServices.Factory.get();
	private KieContainer kContainer = ks.getKieClasspathContainer();
	private KieSession kSession = kContainer.newKieSession("ksession-rules");

	private  double bpm = 0.0;
	private  int numCiclo = 0;
	private final DecimalFormat dosDecimales = new DecimalFormat("#.##");

	// Para calcular el valor medio de todos los segmentos, intervalos y del complejo QRS
	private Duration duracion_total_QRS_complex;
	private Duration duracion_total_PR_interval;
	private Duration duracion_total_RR_interval;
	private Duration duracion_total_PR_segment;
	private Duration duracion_total_ST_interval;
	private Duration duracion_total_ST_segment;
	private Duration duracion_total_QT_interval;
	private LinkedList<Alarm> listaAlarmas = new LinkedList<Alarm>();
	private boolean insertarAlarma;
	private RulesTracking rulesTracking;

	public Rete() {
		rulesTracking = new RulesTracking();
		insertarDuraciones();
	}

	private void insertarDuraciones() {
		duracion_total_QRS_complex = new Duration(0, DurationType.QRSCOMPLEX);
		duracion_total_PR_interval = new Duration(0, DurationType.PRINTERVAL);
		duracion_total_RR_interval = new Duration(0, DurationType.RRINTERVAL);
		duracion_total_PR_segment = new Duration(0, DurationType.PRSEGMENT);
		duracion_total_ST_interval = new Duration(0, DurationType.STINTERVAL);
		duracion_total_ST_segment = new Duration(0, DurationType.STSEGMENT);
		duracion_total_QT_interval = new Duration(0, DurationType.QTINTERVAL);

		kSession.insert(duracion_total_QRS_complex);
		kSession.insert(duracion_total_PR_interval);
		kSession.insert(duracion_total_RR_interval);
		kSession.insert(duracion_total_PR_segment);
		kSession.insert(duracion_total_ST_interval);
		kSession.insert(duracion_total_ST_segment);
		kSession.insert(duracion_total_QT_interval);
		kSession.insert(rulesTracking);
	}


	public Duration getDuracion_total_QRS_complex() {
		return duracion_total_QRS_complex;
	}

	public KieServices getKs() {
		return ks;
	}

	public void setKs(KieServices ks) {
		this.ks = ks;
	}

	public KieContainer getkContainer() {
		return kContainer;
	}

	public void setkContainer(KieContainer kContainer) {
		this.kContainer = kContainer;
	}

	public KieSession getkSession() {
		return kSession;
	}

	public void setkSession(KieSession kSession) {
		this.kSession = kSession;
	}

	public  double getBpm() {
		return bpm;
	}

	public  int getNumCiclo() {
		return numCiclo;
	}

	public void addWave(String type, int start, int end, double peak, int numCiclo){
		kSession.insert(new Wave(WaveType.valueOf(type), start, end,  peak, numCiclo));
	}

	/**
	 * Metodo que avanza un ciclo en nuestro ECG
	 */
	public  void avanzarCiclo() {
		numCiclo++;
	}

	/**
	 * Metodo que resetea nuestra base de hechos y variables del sistema para
	 * cargar otro fichero ECG
	 *
	 */
	public void resetear() {
		//TODO No resetea bien
		//rete.reset();
		numCiclo = 0;
		bpm = 0;
		//rete.store("duracion_total_RR", new Value(0, RU.INTEGER));
		//rete.store("duracion_total_QRS_complex", new Value(0, RU.INTEGER));
		//rete.store("duracion_total_PR_interval", new Value(0, RU.INTEGER));
		//rete.store("duracion_total_PR_segment", new Value(0, RU.INTEGER));
		//rete.store("duracion_total_ST_interval", new Value(0, RU.INTEGER));
		//rete.store("duracion_total_ST_segment", new Value(0, RU.INTEGER));
		//rete.store("duracion_total_QT_interval", new Value(0, RU.INTEGER));
		// introducimos un hecho para que lleve el heart rate 
		//heartRateF = new Fact(heartRate);
		//heartRateF.setSlotValue("bpm", new Value(bpm, RU.FLOAT));
		//rete.assertFact(heartRateF); //Insertamos el hecho en la BH
	}

	/**
	 * Metodo que ejecuta el SBR para que empiece a inferir
	 */
	public void run() {
		kSession.fireAllRules();
		actualizarBPMFinal();
	}

	/**
	 * Metodo que una vez que ha leido todos los ciclos, nos calcula el BPM
	 * medio y nos dice si hay alguna enfermedad relaicionada con el ritmo
	 * cardiaco
	 */
	private void actualizarBPMFinal() {
		// Actualizamos al nuevo heart rate
		/* 
         Obtenemos la suma total de todos los intervalos RR de nuestro ECG
         ese valor sería la duración media por intervalo RR, tenemos ahora que calcular los BPM
         para eso tenemos que dividir 60000 (un minuto) por los ms del intervalo RR medio 
		 */
		bpm = 60000 / (duracion_total_RR_interval.getDuration() / numCiclo);
		//TODO De momento solo calcula el bpm, pero no nos sirve para detectar enfermedades asociadas a su valor aún
		//rete.modify(heartRateF, "bpm", new Value(bpm, RU.FLOAT));
		//kSession.fireAllRules();
	}

	/**
	 * Metodo que devuelve en formato de texto el diagnostico inferido
	 *
	 * @param idioma Idioma en el que se devuelve el diagnostico. 0 ingles y 1
	 * español
	 * @return El String que contiene el diagnostico
	 */
	public String getDiagnostico(int idioma) {
		QueryResults results = kSession.getQueryResults( "getAbnormalPatterns" ); 
		for ( QueryResultsRow row : results ) {
			AbnormalPattern abnormalPattern = ( AbnormalPattern ) row.get( "$result" ); //you can retrieve all the bounded variables here
			for (Alarm alarma : listaAlarmas) {
				// si en nuestra alarma ya teniamos alguna de la misma enfermedad
				alarma.addCiclo(abnormalPattern.getSymptom(), abnormalPattern.getCycle(), abnormalPattern.getExtraInfo());
				// añadimos un ciclo nuevo a su lista de ciclos
				insertarAlarma = false;
			}
			if (insertarAlarma) {
				Alarm alarmaAux = new Alarm(String.valueOf(abnormalPattern.getName()));
				alarmaAux.addCiclo(abnormalPattern.getSymptom(), abnormalPattern.getCycle(), abnormalPattern.getExtraInfo());
				listaAlarmas.add(alarmaAux);
			}
			insertarAlarma = true;
		}
		return listaDiagnosticoToString(listaAlarmas, idioma);
	}

	/**
	 * Metodo que recibe una lista de alarmas y genera el string formateado del
	 * diagnostico
	 *
	 * @param listaAlarmas La lista que contiene todas las alarmas
	 * @param idioma Entero que representa el idioma en el que se tiene que
	 * mostrar. 0 ingles, 1 español
	 * @return String del diagnostico formateado
	 */
	public String listaDiagnosticoToString(LinkedList<Alarm> listaAlarmas, int idioma) {
		// TODO Algo no va bien, comparar con el diagnóstico de la práctica en jess para comprobarlo
		String diagnostico;
		if (idioma == 0) {
			if (numCiclo < 1) {
				diagnostico = "ERROR: File not loaded, try to select a new file: \"File -> Load File\".";
			} 
			else {

				diagnostico = "ECG results:\n";
				Set<String> conjuntoClavesSintomas;

				Set<Integer> conjuntoClavesCiclos;
				if (listaAlarmas.isEmpty()) {
					diagnostico += "Not anomalies were found.";
				} else {
					for (Alarm alarma : listaAlarmas) {
						diagnostico += "Symptoms of " + alarma.getEnfermedad_produce() + " produced by:\n";
						conjuntoClavesSintomas = alarma.getClaves();
						// recorremos el conjunto de claves
						for (String claveSintoma : conjuntoClavesSintomas) {
							diagnostico += "\t" + claveSintoma + " in \n";
							conjuntoClavesCiclos = alarma.getConjuntoClavesCiclos(claveSintoma);
							for (int claveCiclo : conjuntoClavesCiclos) {
								// ponemos a 0 el ciclo cuando se trata de los BPM, ya que no tienen ciclo
								if (claveCiclo != 0) {
									diagnostico += "\t\t Cycle:" + claveCiclo + " with this value: " + dosDecimales.format(alarma.getValorInfoExtra(claveSintoma, claveCiclo)) + "\n";
								} else {
									diagnostico += "\t\t the ECG with a BPM of " + dosDecimales.format(alarma.getValorInfoExtra(claveSintoma, claveCiclo)) + "\n";
								}
							}
						}
					}
				}
			}
			return diagnostico;
		} else {
			if (numCiclo < 1) {
				diagnostico = "ERROR: Fichero no cargado, prueba a seleccionar un nuevo archivo: \"Archivo -> Cargar archivo\".";
			} else {
				diagnostico = "Resultados del ECG:\n";
				Set<String> conjuntoClavesSintomas;

				Set<Integer> conjuntoClavesCiclos;
				if (listaAlarmas.isEmpty()) {
					diagnostico += "No se encontraron anomalías.";
				} else {
					for (Alarm alarma : listaAlarmas) {
						diagnostico += "Síntomas de " + alarma.getEnfermedad_produce() + " producido por:\n";
						conjuntoClavesSintomas = alarma.getClaves();
						// recorremos el conjunto de claves
						for (String claveSintoma : conjuntoClavesSintomas) {
							diagnostico += "\t" + claveSintoma + " en \n";
							conjuntoClavesCiclos = alarma.getConjuntoClavesCiclos(claveSintoma);
							for (int claveCiclo : conjuntoClavesCiclos) {
								// ponemos a 0 el ciclo cuando se trata de los BPM, ya que no tienen ciclo
								if (claveCiclo != 0) {
									diagnostico += "\t\t Ciclo:" + claveCiclo + " con este valor: " + dosDecimales.format(alarma.getValorInfoExtra(claveSintoma, claveCiclo)) + "\n";
								} else {
									diagnostico += "\t\t el ECG con un valor BPM de " + dosDecimales.format(alarma.getValorInfoExtra(claveSintoma, claveCiclo)) + "\n";
								}
							}
						}
					}
				}
			}
			return diagnostico;
		}
	}

	/**
	 * Metodo que calcula los valores medios de los segmentos, intervalos y
	 * complejo QRS y los devuelve en forma de string.
	 *
	 * @param idioma Idioma en el que se muestran los valores medios. 0 Ingles 1
	 * Español
	 * @return string con los valores medios.
	 */
	public String getValoresMedios(int idioma) {
		String valoresMedios = "";
		if (idioma == 0) {
			if (numCiclo > 0) {
				valoresMedios = "\n\nAdditional information from the ECG:";
				valoresMedios += "\n   -Cycle average length = " + (duracion_total_RR_interval.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -QRS Complex average length = " + (duracion_total_QRS_complex.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -PR interval average length = " + (duracion_total_PR_interval.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -PR segment average length = " + (duracion_total_PR_segment.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -ST interval average length = " + (duracion_total_ST_interval.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -ST segment average length = " + (duracion_total_ST_segment.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -QT interval average length = " + (duracion_total_QT_interval.getDuration() / numCiclo) + " ms.";
			}

			return valoresMedios;
		} else {
			if (numCiclo > 0) {
				valoresMedios = "\n\nAdditional information from the ECG:";
				valoresMedios += "\n   -Longitud media del ciclo = " + (duracion_total_RR_interval.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -Longitud media del complejo QRS = " + (duracion_total_QRS_complex.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -Longitud media del intervalo PR = " + (duracion_total_PR_interval.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -Longitud media del segmento PR = " + (duracion_total_PR_segment.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -Longitud media del intervalo ST = " + (duracion_total_ST_interval.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -Longitud media del segmento ST = " + (duracion_total_ST_segment.getDuration() / numCiclo) + " ms.";
				valoresMedios += "\n   -Longitud media del intervalo QT = " + (duracion_total_QT_interval.getDuration() / numCiclo) + " ms.";
			}
			return valoresMedios;
		}
	}

	/**
	 * Metodo que muestra las reglas de nuestro sistema
	 *
	 * @return Un String que lleva las reglas de nuestro sistema
	 */
	public String getRules() {
		//TODO
		/*
		Iterator listaReglas = rete.listDefrules();
		String reglas = "";
		while (listaReglas.hasNext()) {
			reglas += listaReglas.next() + "\n";
		}
		return reglas;
		 */
		return "TIENES QUE HACER que muestre las reglas declaradas";
	}

	/**
	 * Metodo que muestra los hechos de nuestro sistema
	 *
	 * @return El String que contiene los hechos del sistema
	 */
	public String getFacts() {
		String hechos = "";
		Collection<? extends Object> objetos = kSession.getObjects();
		for(Object objeto: objetos){
			hechos+= objeto.toString() + "\n";
		}
		return hechos;
	}

	/**
	 * Metodo que muestra la agenda de nuestro sistema
	 *
	 * @return La agenda en forma de texto
	 */
	public String getAgenda() {
		// TODO así no muestra la agenda, sino que muestra las reglas disparadas (vamos, al revés que show agenda, 
		// que indica las que están listas para ser disparadas)
		String agenda = "";
		for(String rule: rulesTracking.getTracking()){
			if(!agenda.contains(rule))
				agenda += rule + "\n";
		}
		return agenda;
	}

}

