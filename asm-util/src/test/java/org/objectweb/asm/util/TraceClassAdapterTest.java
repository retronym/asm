// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.
package org.objectweb.asm.util;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.test.AsmTest;

/**
 * CheckClassAdapter tests.
 *
 * @author Eric Bruneton
 */
public class TraceClassAdapterTest extends AsmTest {

  @Test
  public void testTraceClassVisitor() throws Exception {
    PrintStream err = System.err;
    PrintStream out = System.out;
    System.setErr(new PrintStream(new ByteArrayOutputStream()));
    System.setOut(new PrintStream(new ByteArrayOutputStream()));
    try {
      String s = getClass().getName();
      Textifier.main(new String[0]);
      Textifier.main(new String[] {"-debug"});
      Textifier.main(new String[] {s});
      Textifier.main(new String[] {"-debug", s});
      Textifier.main(new String[] {"-debug", "java.util.function.Predicate"});
      Textifier.main(new String[] {"java.lang.Object"});
    } finally {
      System.setErr(err);
      System.setOut(out);
    }
  }

  /**
   * Tests that classes are unchanged with a ClassReader->TraceClassAdapter->ClassWriter transform.
   */
  @ParameterizedTest
  @MethodSource(ALL_CLASSES_AND_LATEST_API)
  public void testTrace(PrecompiledClass classParameter, Api apiParameter) {
    byte[] classFile = classParameter.getBytes();
    ClassReader classReader = new ClassReader(classFile);
    ClassWriter classWriter = new ClassWriter(0);
    ClassVisitor classVisitor =
        new TraceClassVisitor(classWriter, new PrintWriter(new CharArrayWriter()));
    classReader.accept(classVisitor, new Attribute[] {new Comment(), new CodeComment()}, 0);
    assertThatClass(classWriter.toByteArray()).isEqualTo(classFile);
  }
}
