package org.nescent.feed

import scala.collection.JavaConversions._
import scala.collection.Set

import org.semanticweb.elk.owlapi.ElkReasonerFactory
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.AddImport
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom
import org.semanticweb.owlapi.model.OWLAnnotationProperty
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLClass
import org.semanticweb.owlapi.model.OWLObjectProperty
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom

object OntologyUtil {

  val factory = OWLManager.getOWLDataFactory

  def combineWithImports(ontology: OWLOntology): OWLOntology = {
    val manager = ontology.getOWLOntologyManager
    val allAxioms = ontology.getImportsClosure.flatMap(_.getAxioms)
    manager.createOntology(allAxioms)
  }

  def redundantSubClassAxioms(ontology: OWLOntology): Set[OWLSubClassOfAxiom] = {
    val reasoner = new ElkReasonerFactory().createReasoner(ontology)
    val axioms = for {
      aClass <- ontology.getClassesInSignature(true) if !aClass.isOWLThing && !aClass.isOWLNothing
      superClass <- reasoner.getSuperClasses(aClass, false).getFlattened if !superClass.isOWLThing && !superClass.isOWLNothing
    } yield factory.getOWLSubClassOfAxiom(aClass, superClass)
    reasoner.dispose()
    axioms
  }

  def redundantPropertyRestrictionsAsAnnotations(property: OWLObjectProperty, ontology: OWLOntology): Set[OWLAnnotationAssertionAxiom] = {
    val manager = ontology.getOWLOntologyManager
    val annotationProperty = propertyToClassRelation(property)
    val equivalenceAxioms = for {
      aClass <- ontology.getClassesInSignature(true) if !aClass.isOWLThing && !aClass.isOWLNothing
    } yield factory.getOWLEquivalentClassesAxiom(classToRestrictionClass(property, aClass), factory.getOWLObjectSomeValuesFrom(property, aClass))
    val equivalences = manager.createOntology(equivalenceAxioms.toSet[OWLAxiom])
    manager.applyChange(new AddImport(equivalences, factory.getOWLImportsDeclaration(ontology.getOntologyID.getOntologyIRI)))
    val reasoner = new ElkReasonerFactory().createReasoner(equivalences)
    val axioms = for {
      aClass <- ontology.getClassesInSignature(true) if !aClass.isOWLThing && !aClass.isOWLNothing
      subject <- reasoner.getSubClasses(classToRestrictionClass(property, aClass), false).getFlattened if !subject.isOWLThing && !subject.isOWLNothing
    } yield factory.getOWLAnnotationAssertionAxiom(annotationProperty, subject.getIRI, aClass.getIRI)
    reasoner.dispose()
    axioms
  }

  def classToRestrictionClass(property: OWLObjectProperty, aClass: OWLClass): OWLClass = {
    val newIRI = IRI.create(property.getIRI.toString + "_some_" + aClass.getIRI.toString)
    factory.getOWLClass(newIRI)
  }

  def propertyToClassRelation(property: OWLObjectProperty): OWLAnnotationProperty = {
    val newIRI = IRI.create(property.getIRI.toString + "_some")
    factory.getOWLAnnotationProperty(newIRI)
  }

}