package edu.illinois.ncsa.daffodil.dsom

/* Copyright (c) 2012-2013 Tresys Technology, LLC. All rights reserved.
 *
 * Developed by: Tresys Technology, LLC
 *               http://www.tresys.com
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal with
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimers.
 * 
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimers in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *  3. Neither the names of Tresys Technology, nor the names of its contributors
 *     may be used to endorse or promote products derived from this Software
 *     without specific prior written permission.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE
 * SOFTWARE.
 */

import java.math.BigInteger
import scala.xml.Node
import edu.illinois.ncsa.daffodil.exceptions.Assert
import edu.illinois.ncsa.daffodil.util.Enum
import scala.util.matching.Regex

trait Facets { self: SimpleTypeDefBase =>
  import Facet._
  import edu.illinois.ncsa.daffodil.dsom.FacetTypes._

  def retrieveFacetValueFromRestrictionBase(xml: Node, facetName: Facet.Type): String = {
    val res = xml \\ "restriction" \ facetName.toString() \ "@value"
    if (res.length > 0) res.head.text else ""
  }
  def retrieveFacetValuesFromRestrictionBase(xml: Node, facetName: Facet.Type): Seq[String] = {
    val res = xml \\ "restriction" \ facetName.toString() \\ "@value"
    if (res.length > 0) res.map(n => n.text).toList else List.empty
  }
  def enumeration(xml: Node): Seq[String] = { retrieveFacetValuesFromRestrictionBase(xml, Facet.enumeration) }
  def fractionDigits(xml: Node): String = { retrieveFacetValueFromRestrictionBase(xml, Facet.fractionDigits) }
  def maxExclusive(xml: Node): String = { retrieveFacetValueFromRestrictionBase(xml, Facet.maxExclusive) }
  def maxInclusive(xml: Node): String = { retrieveFacetValueFromRestrictionBase(xml, Facet.maxInclusive) }
  def maxLength(xml: Node): String = { retrieveFacetValueFromRestrictionBase(xml, Facet.maxLength) }
  def minExclusive(xml: Node): String = { retrieveFacetValueFromRestrictionBase(xml, Facet.minExclusive) }
  def minInclusive(xml: Node): String = { retrieveFacetValueFromRestrictionBase(xml, Facet.minInclusive) }
  def minLength(xml: Node): String = { retrieveFacetValueFromRestrictionBase(xml, Facet.minLength) }
  def pattern(xml: Node): Seq[String] = {
    // Patterns are OR'd locally, AND'd remotely
    retrieveFacetValuesFromRestrictionBase(xml, Facet.pattern).map(p => p)
  }
  def totalDigits(xml: Node): String = { retrieveFacetValueFromRestrictionBase(xml, Facet.totalDigits) }
  def whitespace(xml: Node): String = { retrieveFacetValueFromRestrictionBase(xml, Facet.whiteSpace) }

  lazy val localPatternValue: String = {
    // Patterns within a type are OR'd
    // per http://www.xfront.com/XML-Schema-library/papers/Algorithm-for-Merging-a-simpleType-Dependency-Chain.pdf
    //
    // Assumed to be valid RegEx
    val patterns = pattern(xml)
    patterns.mkString("|")
  }
  lazy val localMinInclusiveValue: String = minInclusive(xml)
  lazy val localMaxInclusiveValue: String = maxInclusive(xml)
  lazy val localMinExclusiveValue: String = minExclusive(xml)
  lazy val localMaxExclusiveValue: String = maxExclusive(xml)
  lazy val localMinLengthValue: String = minLength(xml)
  lazy val localMaxLengthValue: String = maxLength(xml)
  lazy val localTotalDigitsValue: String = totalDigits(xml)
  lazy val localFractionDigitsValue: String = fractionDigits(xml)
  lazy val localEnumerationValue: String = {
    // Enumerations are OR'd
    // May be empty string
    // Must be unique
    val enumerations = enumeration(xml)
    val distinctEnums = enumerations.distinct
    if (enumerations.size != distinctEnums.size) context.SDE("Enumerations must be unique!")
    // Not a regular expression, but we plan to use it as one
    // so we must escape characters that can be interpreted as RegEx
    enumerations.map(s => escapeForRegex(s)).mkString("|")
  }
  lazy val localWhitespaceValue: String = {
    whitespace(xml)
    context.SDE("whitespaceValue is not implemented for DFDL v1.0 schemas but reserved for future use.")
  }

  private def escapeForRegex(s: String): String = {
    val sb = new StringBuilder
    s.foreach(c => {
      c match {
        case '[' => sb.append("\\[")
        case '\\' => sb.append("\\\\")
        case '^' => sb.append("\\^")
        case '$' => sb.append("\\$")
        case '.' => sb.append("\\.")
        case '|' => sb.append("\\|")
        case '?' => sb.append("\\?")
        case '*' => sb.append("\\*")
        case '+' => sb.append("\\+")
        case '(' => sb.append("\\(")
        case ')' => sb.append("\\)")
        case '{' => sb.append("\\{")
        case '}' => sb.append("\\}")
        case x => sb.append(x)
      }
    })
    sb.toString()
  }

  lazy val hasEnumeration: Boolean = (localEnumerationValue.length > 0) || (getRemoteFacetValues(Facet.enumeration).size > 0)
  lazy val hasPattern: Boolean = (localPatternValue.length > 0) || (getRemoteFacetValues(Facet.pattern).size > 0)
  lazy val hasMinLength: Boolean = (localMinLengthValue != "") || (getRemoteFacetValues(Facet.minLength).size > 0)
  lazy val hasMaxLength: Boolean = (localMaxLengthValue != "") || (getRemoteFacetValues(Facet.maxLength).size > 0)
  lazy val hasMinInclusive: Boolean = (localMinInclusiveValue != "") || (getRemoteFacetValues(Facet.minInclusive).size > 0)
  lazy val hasMaxInclusive: Boolean = (localMaxInclusiveValue != "") || (getRemoteFacetValues(Facet.maxInclusive).size > 0)
  lazy val hasMinExclusive: Boolean = (localMinExclusiveValue != "") || (getRemoteFacetValues(Facet.minExclusive).size > 0)
  lazy val hasMaxExclusive: Boolean = (localMaxExclusiveValue != "") || (getRemoteFacetValues(Facet.maxExclusive).size > 0)
  lazy val hasTotalDigits: Boolean = (localTotalDigitsValue != "") || (getRemoteFacetValues(Facet.totalDigits).size > 0)
  lazy val hasFractionDigits: Boolean = (localFractionDigitsValue != "") || (getRemoteFacetValues(Facet.fractionDigits).size > 0)

  lazy val patternValues: Seq[FacetValueR] = {
    val values = combinedBaseFacets.filter { case (f, _) => f == Facet.pattern }
    if (values.size > 0) {
      val res: Seq[FacetValueR] = values.map { case (f, v) => (f, v.r) }
      res
    } else Seq.empty
  }
  lazy val enumerationValues: String = {
    // Should only ever have one set per SimpleType
    val values = combinedBaseFacets.filter { case (f, _) => f == Facet.enumeration }
    if (values.size > 0) {
      val (_, value) = values(0)
      value
    } else context.SDE("Enumeration was not found in this context.")
  }
  // TODO: Tidy up.  Can likely replace getFacetValue with a similar call to combinedBaseFacets
  // as combinedBaseFacets should contain the 'narrowed' values.
  //
  lazy val minLengthValue: java.math.BigDecimal = getFacetValue(localMinLengthValue, Facet.minLength, hasMinLength)
  lazy val maxLengthValue: java.math.BigDecimal = getFacetValue(localMaxLengthValue, Facet.maxLength, hasMaxLength)
  lazy val minInclusiveValue: java.math.BigDecimal = getFacetValue(localMinInclusiveValue, Facet.minInclusive, hasMinInclusive)
  lazy val maxInclusiveValue: java.math.BigDecimal = getFacetValue(localMaxInclusiveValue, Facet.maxInclusive, hasMaxInclusive)
  lazy val minExclusiveValue: java.math.BigDecimal = getFacetValue(localMinExclusiveValue, Facet.minExclusive, hasMinExclusive)
  lazy val maxExclusiveValue: java.math.BigDecimal = getFacetValue(localMaxExclusiveValue, Facet.maxExclusive, hasMaxExclusive)
  lazy val totalDigitsValue: java.math.BigDecimal = getFacetValue(localTotalDigitsValue, Facet.totalDigits, hasTotalDigits)
  lazy val fractionDigitsValue: java.math.BigDecimal = getFacetValue(localFractionDigitsValue, Facet.fractionDigits, hasFractionDigits)

  private def errorOnLocalLessThanBaseFacet(local: Long, base: Long, theFacetType: Facet.Type) = {
    if (local < base) context.SDE("SimpleTypes: The local %s (%s) was less than the base %s (%s) ", theFacetType, local, theFacetType, base)
  }
  private def errorOnLocalGreaterThanBaseFacet(local: Long, base: Long, theFacetType: Facet.Type) = {
    if (local > base) context.SDE("SimpleTypes: The local %s (%s) was greater than the base %s (%s) ", theFacetType, local, theFacetType, base)
  }
  private def errorOnLocalLessThanBaseFacet(local: BigInteger,
    base: BigInteger, theFacetType: Facet.Type) = {
    val res = local.compareTo(base)
    if (res < 0) context.SDE("SimpleTypes: The local %s (%s) was less than the base %s (%s) ",
      theFacetType, local, theFacetType, base)
  }
  private def errorOnLocalGreaterThanBaseFacet(local: BigInteger,
    base: BigInteger, theFacetType: Facet.Type) = {
    val res = local.compareTo(base)
    if (res > 0) context.SDE("SimpleTypes: The local %s (%s) was greater than the base %s (%s) ",
      theFacetType, local, theFacetType, base)
  }
  private def errorOnLocalLessThanBaseFacet(local: java.math.BigDecimal,
    base: java.math.BigDecimal, theFacetType: Facet.Type) = {
    val res = local.compareTo(base)
    if (res < 0) context.SDE("SimpleTypes: The local %s (%s) was less than the base %s (%s) ",
      theFacetType, local, theFacetType, base)
  }
  private def errorOnLocalGreaterThanBaseFacet(local: java.math.BigDecimal,
    base: java.math.BigDecimal, theFacetType: Facet.Type) = {
    val res = local.compareTo(base)
    if (res > 0) context.SDE("SimpleTypes: The local %s (%s) was greater than the base %s (%s) ",
      theFacetType, local, theFacetType, base)
  }

  private def getRemoteFacets(theFacetType: Facet.Type): Seq[FacetValueR] = {
    val remoteValues = remoteBaseFacets.filter { case (f, _) => f == theFacetType }
    if (remoteValues.size > 0) {
      val res: Seq[FacetValueR] = remoteValues.map { case (f, v) => (f, v.r) }
      res
    } else Seq.empty
  }

  private def getRemoteFacetValues(theFacetType: Facet.Type): Seq[FacetValue] = {
    val res = remoteBaseFacets.filter { case (f, _) => f == theFacetType }
    res
  }

  private def getRemoteFacetValue(theFacetType: Facet.Type): String = {
    // Filtering works more appropriately here
    val res = remoteBaseFacets.filter { case (f, v) => f == theFacetType }
    if (res.size > 0) {
      val (_, theFacetValue) = res(0)
      return theFacetValue
    }
    "" // Indicates the facet doesn't exist
  }

  private def evaluateFacet(check: (Long, Long, Facet.Type) => Unit, theFacetType: Facet.Type, theLocalFacet: Long) = {
    val remoteFacetValues = getRemoteFacetValues(theFacetType)
    if (remoteFacetValues.size > 0) {
      val (_, remoteValues) = remoteFacetValues(0)
      val theRemoteFacet = remoteValues(0).toLong
      check(theLocalFacet, theRemoteFacet, theFacetType)
    }
    theLocalFacet
  }

  private def getFacetValue(theLocalValue: String, theRemoteValue: String, theType: Facet.Type, exists: Boolean): java.math.BigDecimal = {
    if (!exists) context.SDE("The facet %s was not found.", theType)
    else if (theLocalValue != "" && theRemoteValue != "") {
      val resFacet = doNumericFacetNarrowing(theLocalValue, theRemoteValue, theType)
      new java.math.BigDecimal(resFacet)
    } else if (theLocalValue != "") {
      checkValueSpaceFacetRange(theLocalValue, theType)
    } else {
      checkValueSpaceFacetRange(theRemoteValue, theType)
    }
  }

  private def getFacetValue(theLocalValue: String, theType: Facet.Type, exists: Boolean): java.math.BigDecimal = {
    val remoteFacets = getRemoteFacetValues(theType)
    if (!exists) context.SDE("The facet %s was not found.", theType)
    else if (theLocalValue != "" && remoteFacets.size > 0) {
      val (_, remoteValue) = getRemoteFacetValues(theType)(0)
      val resFacet = doNumericFacetNarrowing(theLocalValue, remoteValue, theType)
      new java.math.BigDecimal(resFacet)
    } else if (theLocalValue != "") {
      checkValueSpaceFacetRange(theLocalValue, theType)
    } else {
      val (_, remoteValue) = remoteFacets(0)
      checkValueSpaceFacetRange(remoteValue, theType)
    }
  }

  private def narrowNonNegativeFacets(localFacet: String, remoteFacet: String, facetType: Facet.Type): String = {
    val theLocalFacet = new BigInteger(localFacet)
    val theRemoteFacet = new BigInteger(remoteFacet)
    if (theLocalFacet.signum() != 1) context.SDE("The %s facet must be a non-negative integer.", facetType)
    facetType match {
      case Facet.minLength => {
        errorOnLocalLessThanBaseFacet(theLocalFacet, theRemoteFacet, facetType)
        localFacet
      }
      case Facet.maxLength | Facet.fractionDigits => {
        errorOnLocalGreaterThanBaseFacet(theLocalFacet, theRemoteFacet, facetType)
        localFacet
      }
      case _ => {
        val errMsg = "narrowNonNegativeFacets is not valid for %s facet".format(facetType)
        Assert.usageError(errMsg)
      }
    }
  }

  private def narrowPositiveIntegerFacets(localFacet: String, remoteFacet: String, facetType: Facet.Type): String = {
    val theLocalFacet = new BigInteger(localFacet)
    val theRemoteFacet = new BigInteger(remoteFacet)
    if ((theLocalFacet.signum() != 1) || (theLocalFacet.compareTo(BigInteger.ZERO) == 0)) context.SDE("The %s facet must be a positive integer.", facetType)
    facetType match {
      case Facet.totalDigits => {
        errorOnLocalGreaterThanBaseFacet(theLocalFacet, theRemoteFacet, facetType)
        localFacet
      }
      case _ => {
        val errMsg = "narrowPositiveIntegerFacets is not valid for %s facet".format(facetType)
        Assert.usageError(errMsg)
      }
    }
  }

  private def narrowValueSpaceFacets(localFacet: String, remoteFacet: String, facetType: Facet.Type) = {
    val (theLocalFacet, theRemoteFacet) = checkValueSpaceFacetRange(localFacet, remoteFacet, facetType)

    // Made it here so range checks were successful
    // Now just validate/compare local and base/remote facet
    facetType match {
      case Facet.minInclusive => { errorOnLocalLessThanBaseFacet(theLocalFacet, theRemoteFacet, facetType) }
      case Facet.maxInclusive => { errorOnLocalGreaterThanBaseFacet(theLocalFacet, theRemoteFacet, facetType) }
      case Facet.minExclusive => { errorOnLocalLessThanBaseFacet(theLocalFacet, theRemoteFacet, facetType) }
      case Facet.maxExclusive => { errorOnLocalGreaterThanBaseFacet(theLocalFacet, theRemoteFacet, facetType) }
      case _ => {
        val errMsg = "Unrecognized facet type (%s) for narrowing of value-space facets.".format(facetType)
        Assert.usageError(errMsg)
      }
    }
    localFacet
  }

  private def convertFacetToBigDecimal(facet: String): java.math.BigDecimal = {
    self.primitiveType match {
      case PrimType.DateTime => dateToBigDecimal(facet, "uuuu-MM-dd'T'HH:mm:ss.SSSSSSxxx", PrimType.DateTime.toString(), context)
      case PrimType.Date => dateToBigDecimal(facet, "uuuu-MM-ddxxx", PrimType.Date.toString(), context)
      case PrimType.Time => dateToBigDecimal(facet, "HH:mm:ss.SSSSSSxxx", PrimType.Time.toString(), context)
      case _ => new java.math.BigDecimal(facet)
    }
  }

  private def checkValueSpaceFacetRange(localFacet: String, facetType: Facet.Type): java.math.BigDecimal = {
    // Necessary for min/max Inclusive/Exclusive Facets

    // Perform conversions once
    //val theLocalFacet = new java.math.BigDecimal(localFacet)
    val theLocalFacet = convertFacetToBigDecimal(localFacet)

    facetType match {
      case Facet.maxExclusive | Facet.maxInclusive |
        Facet.minExclusive | Facet.minInclusive | Facet.enumeration => {
        // Here we're just doing range checking for the
        // specified primitive type
        primitiveType match {
          case PrimType.Int => {
            if (!isFacetInIntRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of Int range.",
                facetType, localFacet)
            }
          }
          case PrimType.Byte => {
            if (!isFacetInByteRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of Byte range.",
                facetType, localFacet)
            }
          }
          case PrimType.Short => {
            if (!isFacetInShortRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of Short range.",
                facetType, localFacet)
            }
          }
          case PrimType.Long => {
            if (!isFacetInLongRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of Long range.",
                facetType, localFacet)
            }
          }
          case PrimType.Integer => {
            // Unbounded integer
            if (!isFacetInIntegerRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of Integer range.",
                facetType, localFacet)
            }
          }
          case PrimType.UInt => {
            if (!isFacetInUnsignedIntRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of unsigned int range.",
                facetType, localFacet)
            }
          }
          case PrimType.UByte => {
            if (!isFacetInUnsignedByteRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of unsigned byte range.",
                facetType, localFacet)
            }
          }
          case PrimType.UShort => {
            if (!isFacetInUnsignedShortRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of unsigned short range.",
                facetType, localFacet)
            }
          }
          case PrimType.ULong => {
            if (!isFacetInUnsignedLongRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of unsigned long range.",
                facetType, localFacet)
            }
          }
          case PrimType.Double => {
            if (!isFacetInDoubleRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of Double range.",
                facetType, localFacet)
            }
          }
          case PrimType.Float => {
            if (!isFacetInFloatRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of Float range.",
                facetType, localFacet)
            }
          }
          case PrimType.NonNegativeInteger => {
            // Unsigned Unbounded Integer
            if (!isFacetInNonNegativeIntegerRange(theLocalFacet)) {
              context.SDE("%s facet value (%s) was found to be outside of NonNegativeInteger range.",
                facetType, localFacet)
            }
          }
          case PrimType.DateTime => { /* Nothing to do here */ }
          case PrimType.Date => { /* Nothing to do here */ }
          case PrimType.Time => { /* Nothing to do here */ }
          case PrimType.Boolean => notYetImplemented("checkValueSpaceFacetRange - Boolean")
          case PrimType.HexBinary => { /* Nothing to do here */ }
          case PrimType.String => { /* Nothing to do here */ }
          case _ => schemaDefinitionError("checkValueSpaceFacetRange - Unrecognized primitive type: %s", primitiveType.name)
        }
      }
      case _ => { /* Nothing to do */ }
    }
    theLocalFacet
  }

  private def checkValueSpaceFacetRange(localFacet: String,
    remoteFacet: String, facetType: Facet.Type): (java.math.BigDecimal, java.math.BigDecimal) = {
    // Neccessary for min/max Inclusive/Exclusive Facets

    // TODO: I think the performance here can be improved.
    //
    // Consider storing the remoteBaseFacets as their actual evaluated values
    // rather than just as String.  This would prevent us from having to perform
    // the checkValueSpaceFacetRange on the remoteFacet here as it would've already
    // been done in the base. --TRW

    // Perform conversions once
    val theRemoteFacet = checkValueSpaceFacetRange(remoteFacet, facetType) //new java.math.BigDecimal(remoteFacet)
    val theLocalFacet = checkValueSpaceFacetRange(localFacet, facetType)

    (theLocalFacet, theRemoteFacet)
  }

  //  private def isNumInRange(num: java.math.BigDecimal, min: java.math.BigDecimal,
  //    max: java.math.BigDecimal): Boolean = {
  //    val checkMin = num.compareTo(min)
  //    if (checkMin < 0) { return false } // num less than min
  //    val checkMax = num.compareTo(max)
  //    if (checkMax > 0) { return false } // num greater than max
  //    true
  //  }
  private def isFacetInByteRange(facet: java.math.BigDecimal): Boolean = self.isInByteRange(facet)

  private def isFacetInShortRange(facet: java.math.BigDecimal): Boolean = self.isInShortRange(facet)
  private def isFacetInIntRange(facet: java.math.BigDecimal): Boolean = self.isInIntRange(facet)
  private def isFacetInIntegerRange(facet: java.math.BigDecimal): Boolean = self.isInIntegerRange(facet)
  private def isFacetInLongRange(facet: java.math.BigDecimal): Boolean = self.isInLongRange(facet)
  private def isFacetInDoubleRange(facet: java.math.BigDecimal): Boolean = self.isInDoubleRange(facet)
  private def isFacetInFloatRange(facet: java.math.BigDecimal): Boolean = self.isInFloatRange(facet)
  private def isFacetInDecimalRange(facet: java.math.BigDecimal): Boolean = {
    // BigDecimal is unbounded? So nothing outside of its range?
    true
  }
  private def isFacetInNegativeIntegerRange(facet: java.math.BigDecimal): Boolean = self.isInNonNegativeIntegerRange(facet)
  private def isFacetInNonNegativeIntegerRange(facet: java.math.BigDecimal): Boolean = self.isInNonNegativeIntegerRange(facet)
  private def isFacetInUnsignedLongRange(facet: java.math.BigDecimal): Boolean =
    isInUnsignedLongRange(facet)

  private def isFacetInUnsignedIntRange(facet: java.math.BigDecimal): Boolean =
    isInUnsignedIntRange(facet)

  private def isFacetInUnsignedShortRange(facet: java.math.BigDecimal): Boolean =
    isInUnsignedShortRange(facet)

  private def isFacetInUnsignedByteRange(facet: java.math.BigDecimal): Boolean =
    isInUnsignedByteRange(facet)

  protected def doNumericFacetNarrowing(localFacet: String, remoteFacet: String, facetType: Facet.Type) = {
    // Assumes both local and remote facets exist
    // Only for Numeric facets
    //
    // Can likely do narrowing checks here
    //
    // BigInt use compareTo
    //  a negative number, zero, or a positive number as this BigInteger is numerically less than, 
    //  equal to, or greater than o, which must be a BigInteger.
    facetType match {
      case Facet.minLength | Facet.maxLength | Facet.fractionDigits => {
        // Non-negative Integers.  BigInt
        narrowNonNegativeFacets(localFacet, remoteFacet, facetType)
      }
      case Facet.minInclusive | Facet.maxInclusive | Facet.minExclusive | Facet.maxExclusive => {
        // In value-space of base type.  BigDecimal?
        narrowValueSpaceFacets(localFacet, remoteFacet, facetType)
      }
      case Facet.totalDigits => {
        // Positive Integer (value greater than 0). BigInt
        narrowPositiveIntegerFacets(localFacet, remoteFacet, facetType)
      }
      case _ => Assert.usageError("Call to 'doNumericFacetNarrowing' only valid for Numeric Facets.")
    }
  }

  private def getLocalValue(theType: Facet.Type) = {
    val res = localBaseFacets.filter { case (f, v) => f == theType }
    if (res.length > 0) {
      val (_, theFacetValue) = res(0)
      theFacetValue
    } else ""
  }

  protected def getCombinedValue(theType: Facet.Type) = {
    val lValue = getLocalValue(theType)
    val rValue = getRemoteFacetValue(theType)
    val cValue = getFacetValue(lValue, rValue, theType, true)
    cValue
  }

  protected def getCombinedValueEnum = {
    val lValue = getLocalValue(Facet.enumeration)
    val rValue = getRemoteFacetValue(Facet.enumeration)
    lValue.foreach(e => {
      if (rValue.length() > 0 && !rValue.contains(e)) context.SDE("Local enumerations must be a subset of base enumerations.")
    })
    if (lValue.length() > 0) { lValue }
    else { rValue }
  }

}

object Facet extends Enum {
  sealed abstract trait Type extends EnumValueType
  case object enumeration extends Type
  case object fractionDigits extends Type
  case object maxExclusive extends Type
  case object maxInclusive extends Type
  case object maxLength extends Type
  case object minExclusive extends Type
  case object minInclusive extends Type
  case object minLength extends Type
  case object pattern extends Type
  case object totalDigits extends Type
  case object whiteSpace extends Type
}

object FacetTypes {
  // These were defined to make life simpler
  // TODO: Should we modify these to also include the name of the simpleType?
  type Values = String
  type ValuesR = Regex
  type FacetValue = (Facet.Type, Values)
  type FacetValueR = (Facet.Type, ValuesR)
  type ElemFacets = Seq[FacetValue]
  type ElemFacetsR = Seq[FacetValueR]
}