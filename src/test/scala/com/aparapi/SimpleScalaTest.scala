package com.aparapi

import com.aparapi.codegen.Diff
import com.aparapi.internal.model.ClassModel
import com.aparapi.internal.writer.KernelWriter
import org.junit.Test

import scala.util.Random

class SimpleScalaTest {
  def runKernel(inA: Array[Float], inB: Array[Float]): Array[Float] = {
    val result = new Array[Float](inA.length)
    val kernel = new Kernel() {
      override def run() {
        val i = getGlobalId()
        result(i) = ((inA(i) + inB(i)) / (inA(i) / inB(i))) * ((inA(i) - inB(i)) / (inA(
          i) * inB(i))) -
          ((inB(i) - inA(i)) * (inB(i) + inA(i))) * ((inB(i) - inA(i)) / (inB(i) * inA(
            i)))
      }
    }

    kernel.execute(inA.length)
    result
  }

  def generateCL(inA: Array[Float], inB: Array[Float]): String = {
    val result = new Array[Float](inA.length)
    val kernel = new Kernel() {
      override def run() {
        val i = getGlobalId()
        result(i) = ((inA(i) + inB(i)) / (inA(i) / inB(i))) * ((inA(i) - inB(i)) / (inA(
          i) * inB(i))) -
          ((inB(i) - inA(i)) * (inB(i) + inA(i))) * ((inB(i) - inA(i)) / (inB(i) * inA(
            i)))
      }
    }

    val classModel = ClassModel.createClassModel(kernel.getClass)

    val entryPoint = classModel.getEntrypoint("run", kernel)
    KernelWriter.writeToString(entryPoint)
  }

  @Test def testKernel(): Unit = {
    val a = Array.fill(50000)(Random.nextFloat())
    val b = Array.fill(50000)(Random.nextFloat())
    assert(runKernel(a, b).length == 50000)
  }

  @Test def testCL(): Unit = {
    val a = Array.fill(50000)(Random.nextFloat())
    val b = Array.fill(50000)(Random.nextFloat())
    val expected =
      """typedef struct This_s{
                       |   __global float *result$2;
                       |   __global float *inA$2;
                       |   __global float *inB$2;
                       |   int passid;
                       |}This;
                       |int get_pass_id(This *this){
                       |   return this->passid;
                       |}
                       |__kernel void run(
                       |   __global float *result$2,
                       |   __global float *inA$2,
                       |   __global float *inB$2,
                       |   int passid
                       |){
                       |   This thisStruct;
                       |   This* this=&thisStruct;
                       |   this->result$2 = result$2;
                       |   this->inA$2 = inA$2;
                       |   this->inB$2 = inB$2;
                       |   this->passid = passid;
                       |   {
                       |      {
                       |         int i = get_global_id(0);
                       |         this->result$2[i]  = (((this->inA$2[i] + this->inB$2[i]) / (this->inA$2[i] / this->inB$2[i])) * ((this->inA$2[i] - this->inB$2[i]) / (this->inA$2[i] * this->inB$2[i]))) - (((this->inB$2[i] - this->inA$2[i]) * (this->inB$2[i] + this->inA$2[i])) * ((this->inB$2[i] - this->inA$2[i]) / (this->inB$2[i] * this->inA$2[i])));
                       |      }
                       |      return;
                       |   }
                       |}""".stripMargin
    assert(Diff.same(expected, generateCL(a, b)))
  }
}
