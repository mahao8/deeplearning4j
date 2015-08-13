package org.nd4j.api

import org.nd4j.linalg.api.complex.{IComplexNDArray, IComplexNumber}
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j

import _root_.scala.util.control.Breaks._

object Implicits {

  implicit class RichINDArray[A <: INDArray](val underlying: A) extends SliceableNDArray with OperatableNDArray[A] with CollectionLikeNDArray {
    def forall(f: Double => Boolean): Boolean = {
      var result = true
      val lv = underlying.linearView()
      breakable {
        for {
          i <- 0 until lv.length()
        } if (!f(lv.getDouble(i))) {
          result = false
          break()
        }
      }
      result
    }

    def >(d: Double): Boolean = forall(_ > d)

    def <(d: Double): Boolean = forall(_ < d)

    def >=(d: Double): Boolean = forall(_ >= d)

    def <=(d: Double): Boolean = forall(_ <= d)

    def apply(target: IndexRange*): INDArray = subMatrix(target: _*)

    def columnP:ColumnProjectedNDArray = new ColumnProjectedNDArray(underlying)

    def rowP:RowProjectedNDArray = new RowProjectedNDArray(underlying)

    def sliceP:SliceProjectedNDArray = new SliceProjectedNDArray(underlying)
  }

  implicit def rowProjection2NDArray(row:RowProjectedNDArray):INDArray = row.array

  implicit def columnProjection2NDArray(column:ColumnProjectedNDArray):INDArray = column.array

  implicit def sliceProjection2NDArray(sliced:SliceProjectedNDArray):INDArray = sliced.array

  /*
   Avoid using Numeric[T].toDouble(t:T) for sequence transformation in XXColl2INDArray to minimize memory consumption.
   */

  implicit def floatArray2INDArray(s:Array[Float]):FloatArray2INDArray = new FloatArray2INDArray(s)
  implicit def floatColl2INDArray(s:Seq[Float]):FloatArray2INDArray = new FloatArray2INDArray(s.toArray)
  class FloatArray2INDArray(val underlying: Array[Float]) extends AnyVal {
    def mkNDArray(shape: Array[Int], ord: NDOrdering = NDOrdering(Nd4j.order()), offset: Int = 0): INDArray = Nd4j.create(underlying, shape, ord.value, offset)

    def asNDArray(shape: Int*): INDArray = Nd4j.create(underlying, shape.toArray)

    def toNDArray: INDArray = Nd4j.create(underlying)
  }

  implicit def doubleArray2INDArray(s:Array[Double]):DoubleArray2INDArray = new DoubleArray2INDArray(s)
  implicit def doubleArray2CollArray(s:Seq[Double]):DoubleArray2INDArray = new DoubleArray2INDArray(s.toArray)
  class DoubleArray2INDArray(val underlying: Array[Double]) extends AnyVal {
    def mkNDArray(shape: Array[Int], ord: NDOrdering = NDOrdering(Nd4j.order()), offset: Int = 0): INDArray = Nd4j.create(underlying, shape, offset, ord.value)

    def asNDArray(shape: Int*): INDArray = Nd4j.create(underlying, shape.toArray)

    def toNDArray: INDArray = Nd4j.create(underlying)
  }

  implicit def intColl2INDArray(s:Seq[Int]):IntArray2INDArray = new IntArray2INDArray(s.toArray)
  implicit def intArray2INDArray(s:Array[Int]):IntArray2INDArray = new IntArray2INDArray(s)

  class IntArray2INDArray(val underlying: Array[Int]) extends AnyVal {
    def mkNDArray(shape: Array[Int], ord: NDOrdering = NDOrdering(Nd4j.order()), offset: Int = 0): INDArray = Nd4j.create(underlying.map(_.toFloat), shape, ord.value, offset)

    def asNDArray(shape: Int*): INDArray = Nd4j.create(underlying.map(_.toFloat), shape.toArray)

    def toNDArray: INDArray = Nd4j.create(underlying.map(_.toFloat).toArray)
  }

  implicit def complexArray2INDArray(underlying: Array[IComplexNumber]):ComplexArray2INDArray = new ComplexArray2INDArray(underlying)
  implicit def complexColl2INDArray(underlying: Seq[IComplexNumber]):ComplexArray2INDArray = new ComplexArray2INDArray(underlying.toArray)
  class ComplexArray2INDArray(val underlying: Array[IComplexNumber]) extends AnyVal {
    def mkNDArray(shape: Array[Int], ord: NDOrdering = NDOrdering(Nd4j.order()), offset: Int = 0): IComplexNDArray = Nd4j.createComplex(underlying, shape, offset,ord.value)

    def asNDArray(shape: Int*): IComplexNDArray = Nd4j.createComplex(underlying, shape.toArray)

    def toNDArray: IComplexNDArray = Nd4j.createComplex(underlying)
  }

  implicit class floatMtrix2INDArray(val underlying: Seq[Seq[Float]]) extends AnyVal {
    def mkNDArray(ord: NDOrdering): INDArray = Nd4j.create(underlying.map(_.toArray).toArray, ord.value)
    def toNDArray: INDArray = Nd4j.create(underlying.map(_.toArray).toArray)
  }

  implicit class floatArrayMtrix2INDArray(val underlying: Array[Array[Float]]) extends AnyVal {
    def mkNDArray(ord: NDOrdering): INDArray = Nd4j.create(underlying, ord.value)
    def toNDArray: INDArray = Nd4j.create(underlying)
  }

  implicit class doubleMtrix2INDArray(val underlying: Seq[Seq[Double]]) extends AnyVal {
    def mkNDArray(ord: NDOrdering): INDArray = Nd4j.create(underlying.map(_.toArray).toArray, ord.value)
    def toNDArray: INDArray = Nd4j.create(underlying.map(_.toArray).toArray)
  }

  implicit class doubleArrayMtrix2INDArray(val underlying: Array[Array[Double]]) extends AnyVal {
    def mkNDArray(ord: NDOrdering ): INDArray = Nd4j.create(underlying,ord.value)
    def toNDArray: INDArray = Nd4j.create(underlying)
  }

  implicit class intMtrix2INDArray(val underlying: Seq[Seq[Int]]) extends AnyVal {
    def mkNDArray(ord: NDOrdering): INDArray = Nd4j.create(underlying.map(_.map(_.toFloat).toArray).toArray, ord.value)
    def toNDArray: INDArray = Nd4j.create(underlying.map(_.map(_.toFloat).toArray).toArray)
  }

  implicit class intArrayMtrix2INDArray(val underlying: Array[Array[Int]]) extends AnyVal {
    def mkNDArray(ord: NDOrdering): INDArray = Nd4j.create(underlying.map(_.map(_.toFloat)), ord.value)
    def toNDArray: INDArray = Nd4j.create(underlying.map(_.map(_.toFloat)))
  }

  implicit class complexArrayMtrix2INDArray(val underlying: Array[Array[IComplexNumber]]) extends AnyVal {
    def toNDArray: IComplexNDArray = Nd4j.createComplex(underlying)
  }

  implicit class complexCollMtrix2INDArray(val underlying: Seq[Seq[IComplexNumber]]) extends AnyVal {
    def toNDArray: IComplexNDArray = Nd4j.createComplex(underlying.map(_.toArray).toArray)
  }

  implicit class num2Scalar[T](val underlying: T)(implicit ev: Numeric[T]) {
    def toScalar: INDArray = Nd4j.scalar(ev.toDouble(underlying))
  }

  case object -> extends IndexRange

  case object ---> extends IndexRange

  implicit class IntRange(val underlying: Int) extends IndexNumberRange {
    protected[api] override def asRange(max: => Int): DRange = DRange(underlying, underlying, true, 1, max)

    override def toString: String = s"$underlying"
  }

  implicit class TupleRange(val underlying: _root_.scala.Tuple2[Int, Int]) extends IndexNumberRange {
    protected[api] override def asRange(max: => Int): DRange = DRange(underlying._1, underlying._2, false, 1, max)

    override def toString: String = s"${underlying._1}->${underlying._2}"

    def by(i: Int) = new IndexRangeWrapper(underlying._1 until underlying._2 by i)
  }

  implicit class IntRangeFromGen(val underlying: Int) extends AnyVal {
    def -> = IntRangeFrom(underlying)
  }

  implicit class IndexRangeWrapper(val underlying: Range) extends IndexNumberRange {
    protected[api] override def asRange(max: => Int): DRange = DRange.from(underlying, max)

    override def toString: String = s"${underlying.start}->${underlying.end} by ${underlying.step}"
  }

  lazy val NDOrdering = org.nd4j.api.NDOrdering

  implicit def int2ComplexNumberBuilder(underlying: Int): ComplexNumberBuilder[Integer] = new ComplexNumberBuilder[Integer](underlying)

  implicit def float2ComplexNumberBuilder(underlying: Float): ComplexNumberBuilder[java.lang.Float] = new ComplexNumberBuilder[java.lang.Float](underlying)

  implicit def double2ComplexNumberBuilder(underlying: Double): ComplexNumberBuilder[java.lang.Double] = new ComplexNumberBuilder[java.lang.Double](underlying)

  lazy val i = new ImaginaryNumber[Integer](1)
}

private[api] class ComplexNumberBuilder[T <: Number](val value: T) extends AnyVal {
  def +[A <: Number](imaginary: ImaginaryNumber[A]): IComplexNumber = Nd4j.createComplexNumber(value, imaginary.value)

  def *(in: ImaginaryNumber[Integer]): ImaginaryNumber[T] = new ImaginaryNumber[T](value)
}

private[api] class ImaginaryNumber[T <: Number](val value: T) extends AnyVal {
  override def toString: String = s"${value}i"
}

sealed trait IndexNumberRange extends IndexRange {
  protected[api] def asRange(max: => Int): DRange
}

sealed trait IndexRange

case class IntRangeFrom(underlying: Int) extends IndexRange {
  def apply(i: Int): (Int, Int) = (underlying, i)

  override def toString: String = s"$underlying->"
}

private[api] case class DRange(startR: Int, endR: Int, isInclusive: Boolean, step: Int, max: Int) {
  lazy val (start, end) = {
    val start = if (startR >= 0) startR else max + startR
    val diff = if (isInclusive) 0 else if (step >= 0) -1 else +1
    val endInclusive = if (endR >= 0) endR + diff else max + endR + diff
    (start, endInclusive)
  }
  lazy val length = (end - start) / step + 1

  def toList: List[Int] = List.iterate(start, length)(_ + step)

  override def toString: String = s"[$start to $end by $step len:$length]"
}

private[api] object DRange extends {
  def from(r: Range, max: => Int): DRange = DRange(r.start, r.end, r.isInclusive, r.step, max)

  def apply(startR: Int, endR: Int, step: Int): DRange = DRange(startR, endR, false, step, Int.MinValue)
}
