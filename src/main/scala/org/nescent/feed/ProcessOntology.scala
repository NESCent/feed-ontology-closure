package org.nescent.feed

import java.io.File

import scala.collection.JavaConversions._

import org.semanticweb.elk.owlapi.ElkReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI

object ProcessOntology extends App {

  val ontologyFile = args(0)
  val outputFile = args(1)
  val factory = OWLManager.getOWLDataFactory
  val manager = OWLManager.createOWLOntologyManager
  val originalOntology = manager.loadOntologyFromOntologyDocument(new File(ontologyFile))
  val PartOf = factory.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/BFO_0000050"))
  val ontology = OntologyUtil.combineWithImports(originalOntology)
  val subClassAxioms = OntologyUtil.redundantSubClassAxioms(ontology)
  val partOfAxioms = OntologyUtil.redundantPropertyRestrictionsAsAnnotations(PartOf, ontology)
  manager.addAxioms(ontology, subClassAxioms)
  manager.addAxioms(ontology, partOfAxioms)
  manager.saveOntology(ontology, IRI.create(new File(outputFile)))

}