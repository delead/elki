/*
 * This file is part of ELKI:
 * Environment for Developing KDD-Applications Supported by Index-Structures
 *
 * Copyright (C) 2017
 * ELKI Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.lmu.ifi.dbs.elki.math.linearalgebra;


import static de.lmu.ifi.dbs.elki.math.linearalgebra.VMath.*;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test class for the LUDecomposition of the linearalgebra package.
 * <p>
 * As reference if the LU Decomposition is working correctly the LU-decomposition function of python3 scipy 0.18.1-2 package,
 * and octave 4.2.1 were used to calculate the correct result. 
 * 
 * @author Merlin Dietrich
 *
 */
public final class LUDecompositionTest {

  /**
   * Random generated Matrix for testing with dimensions 11 x 7.
   */
  private final static double[][] M = {
    {  78,  70, -54, -89,  -6, -69,  31}, //
    { -50, -48, -16, -88, -81, -45,  89}, //
    {  14, -13,  -6,  44,  24,  20, -18}, //
    {   4, -52,  -9, -44, -20,   2, -28}, //
    {  50,  12,  51,   1, -62,  60, -96}, //
    {  14,  18,  15, -22,  92, -15, -39}, //
    { -90,  60, -10,  52,  66,  52,  -3}, //
    {  95,  59, -30, -87,  85, -21, -49}, //
    {  41,  83,  30, -52,   8,  34, -28}, //
    {  -1, -89, -72, -73,  45, -88, -22}, //
    { -31, -95,  73,   6,   5, -12,  13}, //
  };
  
 /**
  * L result of calculating the LU decomposition of {@link M} with python3 scipy package.
  */ 
  private final static double[][] L_SCIPY = {
      { 1.        ,  0.        ,  0.        ,  0.        ,  0.        ,  0.        ,  0.        }, //
      {-0.94736842,  1.        ,  0.        ,  0.        ,  0.        ,  0.        ,  0.        }, //
      {-0.01052632, -0.76257947,  1.        ,  0.        ,  0.        ,  0.        ,  0.        }, //
      {-0.52631579, -0.1462307 ,  0.36813315,  1.        ,  0.        ,  0.        ,  0.        }, //
      {-0.32631579, -0.65358765, -0.3749352 ,  0.7677448 ,  1.        ,  0.        ,  0.        }, //
      { 0.82105263,  0.18601272,  0.21868464, -0.09101329, -0.59186707,  1.        ,  0.        }, //
      { 0.52631579, -0.164396  , -0.59512147,  0.1561799 ,  0.09255763, -0.91941086,  1.        }, //
      { 0.14736842,  0.08029064, -0.22148233,  0.27560098,  0.50381511,  0.44555568,  0.19642636}, //
      { 0.43157895,  0.49645777, -0.61036129,  0.57202041,  0.14972561, -0.09087813,  0.49771806}, //
      { 0.14736842, -0.18719346,  0.08631724, -0.58064996, -0.06985769, -0.39505982, -0.39690551}, //
      { 0.04210526, -0.47011807,  0.25389263,  0.29254186,  0.10921969, -0.91019314,  0.18053163}};
  /**
   * U result of calculating the LU decomposition of {@link M} with python3 scipy package.
   */
  private final static double[][] U_SCIPY = {
      {  95.        ,   59.        ,  -30.        ,  -87.        ,  85.        ,  -21.        ,  -49.        }, //
      {   0.        ,  115.89473684,  -38.42105263,  -30.42105263, 146.52631579,   32.10526316,  -49.42105263}, //
      {   0.        ,    0.        , -101.61489555,  -97.11425976, 157.63269755,  -63.73823797,  -60.20326975}, //
      {   0.        ,    0.        ,    0.        , -102.48698761, -72.86633297,  -27.89369849,   78.14647026}, //
      {   0.        ,    0.        ,    0.        ,    0.        , 243.54942711,   -0.35149494, -117.85933441}, //
      {   0.        ,    0.        ,    0.        ,    0.        ,   0.        ,  -46.53804406,   30.94536208}, //
      {   0.        ,    0.        ,    0.        ,    0.        ,   0.        ,    0.        ,  -87.0080333 }};
  /**
   * Pivot result of calculating the LU decomposition of {@link M} with python3 scipy package.
   */
  private final static double[][] P_SCIPY = {
      { 0.,  0.,  0.,  0.,  0.,  1.,  0.,  0.,  0.,  0.,  0.}, //
      { 0.,  0.,  0.,  1.,  0.,  0.,  0.,  0.,  0.,  0.,  0.}, //
      { 0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  1.,  0.}, //
      { 0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  1.}, //
      { 0.,  0.,  0.,  0.,  0.,  0.,  1.,  0.,  0.,  0.,  0.}, //
      { 0.,  0.,  0.,  0.,  0.,  0.,  0.,  1.,  0.,  0.,  0.}, //
      { 0.,  1.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.}, //
      { 1.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.}, //
      { 0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  1.,  0.,  0.}, //
      { 0.,  0.,  1.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.}, //
      { 0.,  0.,  0.,  0.,  1.,  0.,  0.,  0.,  0.,  0.,  0.}};

  /**
   * L result of calculating the LU decomposition of {@link M} with octave.
   */
  private static final double[][] L_OCTAVE = {
    {  0.8210526315789474 ,  0.1860127157129882 ,  0.2186846386242156 , -0.09101329428561518, -0.591867071180613  ,  1                  ,  0                  }, //
    { -0.5263157894736842 , -0.1462306993642144 ,  0.3681331450329823 ,  1                  ,  0                  ,  0                  ,  0                  }, //
    {  0.1473684210526316 , -0.1871934604904632 ,  0.08631723842042223, -0.5806499597524488 , -0.06985768896696073, -0.3950598198621239 , -0.3969055120928031 }, //
    {  0.04210526315789474, -0.4701180744777476 ,  0.2538926330467116 ,  0.2925418569997564 ,  0.1092196935604799 , -0.9101931433766953 ,  0.1805316324498147 }, //
    {  0.5263157894736842 , -0.1643960036330608 , -0.595121471602996  ,  0.1561798951463552 ,  0.09255763023216994, -0.9194108577125015 ,  1                  }, //
    {  0.1473684210526316 ,  0.08029064486830155, -0.2214823289654802 ,  0.2756009764906437 ,  0.5038151096249673 ,  0.4455556790404538 ,  0.1964263617267827 }, //
    { -0.9473684210526315 , 1                   ,  0                  ,  0                  ,  0                  ,  0                  ,  0                  }, //
    {  1                  , 0                   ,  0                  ,  0                  ,  0                  ,  0                  ,  0                  }, //
    {  0.4315789473684211 ,  0.496457765667575  , -0.6103612864012585 ,  0.5720204061398988 ,  0.1497256141853258 , -0.09087812747379009,  0.4977180566966914 }, //
    { -0.01052631578947368, -0.7625794732061763 ,  1                  ,  0                  ,  0                  ,  0                  ,  0                  }, //
    { -0.3263157894736842 , -0.6535876475930972 , -0.3749351972684531 ,  0.7677448024074434 ,  1                  ,  0                  ,  0                  }};

  /**
   * U result of calculating the LU decomposition of {@link M} with octave.
   */
  private static final double[][] U_OCTAVE = {
     {95,  59              , -30               , -87               ,  85               , -21                 , -49               }, //
     { 0, 115.8947368421053, -38.42105263157895, -30.42105263157895, 146.5263157894737 ,  32.10526315789474  , -49.42105263157895}, //
     { 0,   0              , -101.6148955495005, -97.11425976385104, 157.6326975476839 , -63.73823796548592  , -60.20326975476839}, //
     { 0,   0              ,    0              , -102.4869876115054, -72.86633296984215, -27.89369849300131  ,  78.14647026224995}, //
     { 0,   0              ,    0              ,    0              , 243.5494271116742 ,  -0.3514949434325931, -117.8593344072991}, //
     { 0,   0              ,    0              ,    0              ,   0               , -46.5380440618577   ,  30.94536207949633}, //
     { 0,   0              ,    0              ,    0              ,   0               ,   0                 , -87.00803330193199}};
  
  /**
   * Testing getL method of the LUDecomposition class with {@link M} as data for testing. 
   */
  @Test
  public void testGetL() {
    // octave returns L in different format so we need to convert with times and P_SCIPY
    assertTrue(almostEquals(L_OCTAVE, times(P_SCIPY,L_SCIPY), 10E-6));
    
    LUDecomposition LU = new LUDecomposition(M);
    
    // assert that octave and scipy function have almost same results
    // octave returns L in different format so we need to convert with times and P_SCIPY
    assertTrue(almostEquals(times(P_SCIPY,LU.getL()), L_OCTAVE, 20E-6));
    
    assertTrue(almostEquals(LU.getL(), L_SCIPY, 20E-6));
  }
  
  /**
   * Testing getU method of the LUDecomposition class with {@link M} as data for testing. 
   */
  @Test
  public void testGetU() {
    // assert that octave and scipy function have almost same results
    assertTrue(almostEquals(U_OCTAVE, U_SCIPY, 10E-6));
    
    LUDecomposition LU = new LUDecomposition(M);
   
    assertTrue(almostEquals(LU.getU(), U_OCTAVE, 20E-6));
    assertTrue(almostEquals(LU.getU(), U_SCIPY, 20E-6));
  } 
  
  /**
   * Testing the getPivot and getDoublePivot methods of the LUDecomposition class with {@link M} as data for testing.
   */
  @Test
  public void testPivot() {
 
    // assert that octave and scipy function have almost same results
    assertTrue(almostEquals(U_OCTAVE, U_SCIPY, 10E-6));
    
    LUDecomposition LU = new LUDecomposition(M);

    // testing getPivot and getDoublePivot
    // transformation of P_scipy into the format of the piv vector as returned by getPivot and getDoublePivot 
    final int[] Piv = { 7, 6 , 9, 1, 10, 0, 4, 5, 8, 2, 3 }; 
    final double[] PivD = { Piv[0], Piv[1], Piv[2], Piv[3], Piv[4], Piv[5], Piv[6], Piv[7], Piv[8], Piv[9], Piv[10] };
    
    assertArrayEquals(Piv, LU.getPivot());
    assertArrayEquals(PivD, LU.getDoublePivot(), 0.);
  }
  
  /**
   * Testing the isNonSigular methods of the LUDecomposition class. 
   */
  @Test
  public void testIsNonSigular() {
    
    LUDecomposition LUM = new LUDecomposition(M);
    assertTrue(LUM.isNonsingular());
    
    final double[][] a = {
        {1,1}, //
        {1,1}, //
        {2,2}, //
    };
    LUDecomposition LUa = new LUDecomposition(a);
    assertFalse(LUa.isNonsingular());
    

  }

  /**
   * Testing the Solve method of the LUDecomposition class. 
   */
  @Test
  public void testSolve() {
    final double[][] a = getMatrix(M, 0, 6, 0, 6);
    
    final double[][] b = {
        {1, 3}, //
        {2, 7}, //
        {5, 7}, //
        {4, 7}, //
        {8,-7}, //
        {1, 7}, //
        {1, 7}, //
    };
    
    LUDecomposition LUa = new LUDecomposition(a);
    
    final double[][] x = LUa.solve(b);
    
    assertTrue(almostEquals(times(a, x), b));
  }
  
  
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  
  /**
   * Testing that the solve method of the LUDecomposition class raises an exception if
   * isNonSingular returns false.
   */
  @Test
  public void testSolveIsNonSingular() {

    thrown.expect(ArithmeticException.class);
    thrown.expectMessage("Matrix is singular.");
    
    final double[][] a = {
        {1,1}, //
        {1,1}, //
    };
    
    
    final double[][] b = {
        {1}, //
        {1}, //
    };
    
    LUDecomposition LU = new LUDecomposition(a);
    
    LU.solve(b);
  }
  
  /**
   * Testing that the solve method of the LUDecomposition class raises an exception if
   * the row dimensions do not agree.
   */
  @Test
  public void testSolveRowDimensionMismatch() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Matrix row dimensions must agree.");
    
    final double[][] B = {{}};
    LUDecomposition LU = new LUDecomposition(M);
    
    LU.solve(B);

  }
  
}