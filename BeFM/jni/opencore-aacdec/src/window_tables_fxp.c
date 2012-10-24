/* ------------------------------------------------------------------
 * Copyright (C) 1998-2009 PacketVideo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 * -------------------------------------------------------------------
 */
/*

 Filename: window_tables_fxp.cpp
 Functions:

  ------------------------------------------------------------------------------
 MODULE DESCRIPTION

    Window tables

        For a sine table with N  points:

            w_left  = sin(pi/N (n + 1/2))     for 0   =< n < N/2

            w_rigth = sin(pi/N (n + 1/2))     for N/2 =< n < N


        For Kaiser-Bessel derived (KBD)

                               n             N/2
            w_left  =  sqrt(( SUM W(p,a) )/( SUM W(p,a) )   for   0   =< n < N/2
                              p=0            p=0


                             N-n-1           N/2
            w_rigth =  sqrt(( SUM W(p,a) )/( SUM W(p,a) )   for   N/2 =< n < N
                              p=0            p=0


            W(p,a) see ISO 14496-3, pag 113

------------------------------------------------------------------------------
 REQUIREMENTS

    This module shall implement the fix point verwion of the windowing tables

------------------------------------------------------------------------------
 REFERENCES

    [1] ISO 14496-3, pag 113

------------------------------------------------------------------------------
*/


/*----------------------------------------------------------------------------
; INCLUDES
----------------------------------------------------------------------------*/
#include "pv_audio_type_defs.h"
#include "window_block_fxp.h"

/*----------------------------------------------------------------------------
; MACROS
; Define module specific macros here
----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------
; DEFINES
; Include all pre-processor statements here. Include conditional
; compile variables also.
----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------
; LOCAL FUNCTION DEFINITIONS
; Function Prototype declaration
----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------
; LOCAL VARIABLE DEFINITIONS
; Variable declaration - defined here and used outside this module
----------------------------------------------------------------------------*/


/*----------------------------------------------------------------------------
; EXTERNAL FUNCTION REFERENCES
; Declare functions defined elsewhere and referenced in this module
----------------------------------------------------------------------------*/

/*----------------------------------------------------------------------------
; EXTERNAL VARIABLES REFERENCES
; Declare variables used in this module but defined elsewhere
----------------------------------------------------------------------------*/


const Int16 Long_Window_sine_fxp[LONG_WINDOW] =
{


    0x0019,  0x004B,  0x007E,  0x00B0,
    0x00E2,  0x0114,  0x0147,  0x0179,
    0x01AB,  0x01DD,  0x0210,  0x0242,
    0x0274,  0x02A7,  0x02D9,  0x030B,
    0x033D,  0x0370,  0x03A2,  0x03D4,
    0x0406,  0x0438,  0x046B,  0x049D,
    0x04CF,  0x0501,  0x0534,  0x0566,
    0x0598,  0x05CA,  0x05FC,  0x062F,
    0x0661,  0x0693,  0x06C5,  0x06F7,
    0x072A,  0x075C,  0x078E,  0x07C0,
    0x07F2,  0x0825,  0x0857,  0x0889,
    0x08BB,  0x08ED,  0x091F,  0x0951,
    0x0984,  0x09B6,  0x09E8,  0x0A1A,
    0x0A4C,  0x0A7E,  0x0AB0,  0x0AE2,
    0x0B14,  0x0B46,  0x0B78,  0x0BAB,
    0x0BDD,  0x0C0F,  0x0C41,  0x0C73,
    0x0CA5,  0x0CD7,  0x0D09,  0x0D3B,
    0x0D6D,  0x0D9F,  0x0DD1,  0x0E03,
    0x0E35,  0x0E67,  0x0E99,  0x0ECA,
    0x0EFC,  0x0F2E,  0x0F60,  0x0F92,
    0x0FC4,  0x0FF6,  0x1028,  0x105A,
    0x108B,  0x10BD,  0x10EF,  0x1121,
    0x1153,  0x1185,  0x11B6,  0x11E8,
    0x121A,  0x124C,  0x127D,  0x12AF,
    0x12E1,  0x1312,  0x1344,  0x1376,
    0x13A8,  0x13D9,  0x140B,  0x143C,
    0x146E,  0x14A0,  0x14D1,  0x1503,
    0x1534,  0x1566,  0x1598,  0x15C9,
    0x15FB,  0x162C,  0x165E,  0x168F,
    0x16C1,  0x16F2,  0x1724,  0x1755,
    0x1786,  0x17B8,  0x17E9,  0x181B,
    0x184C,  0x187D,  0x18AF,  0x18E0,
    0x1911,  0x1942,  0x1974,  0x19A5,
    0x19D6,  0x1A07,  0x1A39,  0x1A6A,
    0x1A9B,  0x1ACC,  0x1AFD,  0x1B2E,
    0x1B60,  0x1B91,  0x1BC2,  0x1BF3,
    0x1C24,  0x1C55,  0x1C86,  0x1CB7,
    0x1CE8,  0x1D19,  0x1D4A,  0x1D7B,
    0x1DAC,  0x1DDC,  0x1E0D,  0x1E3E,
    0x1E6F,  0x1EA0,  0x1ED1,  0x1F01,
    0x1F32,  0x1F63,  0x1F94,  0x1FC4,
    0x1FF5,  0x2026,  0x2056,  0x2087,
    0x20B7,  0x20E8,  0x2119,  0x2149,
    0x217A,  0x21AA,  0x21DB,  0x220B,
    0x223C,  0x226C,  0x229C,  0x22CD,
    0x22FD,  0x232E,  0x235E,  0x238E,
    0x23BE,  0x23EF,  0x241F,  0x244F,
    0x247F,  0x24AF,  0x24E0,  0x2510,
    0x2540,  0x2570,  0x25A0,  0x25D0,
    0x2600,  0x2630,  0x2660,  0x2690,
    0x26C0,  0x26F0,  0x2720,  0x274F,
    0x277F,  0x27AF,  0x27DF,  0x280F,
    0x283E,  0x286E,  0x289E,  0x28CD,
    0x28FD,  0x292D,  0x295C,  0x298C,
    0x29BB,  0x29EB,  0x2A1A,  0x2A4A,
    0x2A79,  0x2AA8,  0x2AD8,  0x2B07,
    0x2B37,  0x2B66,  0x2B95,  0x2BC4,
    0x2BF4,  0x2C23,  0x2C52,  0x2C81,
    0x2CB0,  0x2CDF,  0x2D0E,  0x2D3D,
    0x2D6C,  0x2D9B,  0x2DCA,  0x2DF9,
    0x2E28,  0x2E57,  0x2E86,  0x2EB5,
    0x2EE3,  0x2F12,  0x2F41,  0x2F70,
    0x2F9E,  0x2FCD,  0x2FFC,  0x302A,
    0x3059,  0x3087,  0x30B6,  0x30E4,
    0x3113,  0x3141,  0x316F,  0x319E,
    0x31CC,  0x31FA,  0x3229,  0x3257,
    0x3285,  0x32B3,  0x32E1,  0x330F,
    0x333E,  0x336C,  0x339A,  0x33C8,
    0x33F6,  0x3423,  0x3451,  0x347F,
    0x34AD,  0x34DB,  0x3509,  0x3536,
    0x3564,  0x3592,  0x35BF,  0x35ED,
    0x361A,  0x3648,  0x3676,  0x36A3,
    0x36D0,  0x36FE,  0x372B,  0x3759,
    0x3786,  0x37B3,  0x37E0,  0x380E,
    0x383B,  0x3868,  0x3895,  0x38C2,
    0x38EF,  0x391C,  0x3949,  0x3976,
    0x39A3,  0x39D0,  0x39FD,  0x3A29,
    0x3A56,  0x3A83,  0x3AB0,  0x3ADC,
    0x3B09,  0x3B35,  0x3B62,  0x3B8E,
    0x3BBB,  0x3BE7,  0x3C14,  0x3C40,
    0x3C6C,  0x3C99,  0x3CC5,  0x3CF1,
    0x3D1D,  0x3D4A,  0x3D76,  0x3DA2,
    0x3DCE,  0x3DFA,  0x3E26,  0x3E52,
    0x3E7D,  0x3EA9,  0x3ED5,  0x3F01,
    0x3F2D,  0x3F58,  0x3F84,  0x3FB0,
    0x3FDB,  0x4007,  0x4032,  0x405E,
    0x4089,  0x40B5,  0x40E0,  0x410B,
    0x4136,  0x4162,  0x418D,  0x41B8,
    0x41E3,  0x420E,  0x4239,  0x4264,
    0x428F,  0x42BA,  0x42E5,  0x4310,
    0x433B,  0x4365,  0x4390,  0x43BB,
    0x43E5,  0x4410,  0x443B,  0x4465,
    0x448F,  0x44BA,  0x44E4,  0x450F,
    0x4539,  0x4563,  0x458D,  0x45B8,
    0x45E2,  0x460C,  0x4636,  0x4660,
    0x468A,  0x46B4,  0x46DE,  0x4707,
    0x4731,  0x475B,  0x4785,  0x47AE,
    0x47D8,  0x4802,  0x482B,  0x4855,
    0x487E,  0x48A7,  0x48D1,  0x48FA,
    0x4923,  0x494D,  0x4976,  0x499F,
    0x49C8,  0x49F1,  0x4A1A,  0x4A43,
    0x4A6C,  0x4A95,  0x4ABE,  0x4AE6,
    0x4B0F,  0x4B38,  0x4B61,  0x4B89,
    0x4BB2,  0x4BDA,  0x4C03,  0x4C2B,
    0x4C53,  0x4C7C,  0x4CA4,  0x4CCC,
    0x4CF4,  0x4D1D,  0x4D45,  0x4D6D,
    0x4D95,  0x4DBD,  0x4DE5,  0x4E0D,
    0x4E34,  0x4E5C,  0x4E84,  0x4EAB,
    0x4ED3,  0x4EFB,  0x4F22,  0x4F4A,
    0x4F71,  0x4F99,  0x4FC0,  0x4FE7,
    0x500E,  0x5036,  0x505D,  0x5084,
    0x50AB,  0x50D2,  0x50F9,  0x5120,
    0x5147,  0x516D,  0x5194,  0x51BB,
    0x51E2,  0x5208,  0x522F,  0x5255,
    0x527C,  0x52A2,  0x52C8,  0x52EF,
    0x5315,  0x533B,  0x5361,  0x5387,
    0x53AE,  0x53D4,  0x53FA,  0x541F,
    0x5445,  0x546B,  0x5491,  0x54B7,
    0x54DC,  0x5502,  0x5527,  0x554D,
    0x5572,  0x5598,  0x55BD,  0x55E2,
    0x5608,  0x562D,  0x5652,  0x5677,
    0x569C,  0x56C1,  0x56E6,  0x570B,
    0x5730,  0x5754,  0x5779,  0x579E,
    0x57C2,  0x57E7,  0x580C,  0x5830,
    0x5854,  0x5879,  0x589D,  0x58C1,
    0x58E5,  0x590A,  0x592E,  0x5952,
    0x5976,  0x599A,  0x59BD,  0x59E1,
    0x5A05,  0x5A29,  0x5A4C,  0x5A70,
    0x5A94,  0x5AB7,  0x5ADA,  0x5AFE,
    0x5B21,  0x5B44,  0x5B68,  0x5B8B,
    0x5BAE,  0x5BD1,  0x5BF4,  0x5C17,
    0x5C3A,  0x5C5D,  0x5C7F,  0x5CA2,
    0x5CC5,  0x5CE7,  0x5D0A,  0x5D2C,
    0x5D4F,  0x5D71,  0x5D94,  0x5DB6,
    0x5DD8,  0x5DFA,  0x5E1C,  0x5E3E,
    0x5E60,  0x5E82,  0x5EA4,  0x5EC6,
    0x5EE8,  0x5F09,  0x5F2B,  0x5F4D,
    0x5F6E,  0x5F90,  0x5FB1,  0x5FD2,
    0x5FF4,  0x6015,  0x6036,  0x6057,
    0x6078,  0x6099,  0x60BA,  0x60DB,
    0x60FC,  0x611D,  0x613D,  0x615E,
    0x617F,  0x619F,  0x61C0,  0x61E0,
    0x6200,  0x6221,  0x6241,  0x6261,
    0x6281,  0x62A1,  0x62C1,  0x62E1,
    0x6301,  0x6321,  0x6341,  0x6360,
    0x6380,  0x63A0,  0x63BF,  0x63DF,
    0x63FE,  0x641D,  0x643D,  0x645C,
    0x647B,  0x649A,  0x64B9,  0x64D8,
    0x64F7,  0x6516,  0x6535,  0x6554,
    0x6572,  0x6591,  0x65AF,  0x65CE,
    0x65EC,  0x660B,  0x6629,  0x6647,
    0x6666,  0x6684,  0x66A2,  0x66C0,
    0x66DE,  0x66FC,  0x6719,  0x6737,
    0x6755,  0x6772,  0x6790,  0x67AE,
    0x67CB,  0x67E8,  0x6806,  0x6823,
    0x6840,  0x685D,  0x687A,  0x6897,
    0x68B4,  0x68D1,  0x68EE,  0x690B,
    0x6927,  0x6944,  0x6961,  0x697D,
    0x699A,  0x69B6,  0x69D2,  0x69EE,
    0x6A0B,  0x6A27,  0x6A43,  0x6A5F,
    0x6A7B,  0x6A97,  0x6AB2,  0x6ACE,
    0x6AEA,  0x6B05,  0x6B21,  0x6B3C,
    0x6B58,  0x6B73,  0x6B8E,  0x6BAA,
    0x6BC5,  0x6BE0,  0x6BFB,  0x6C16,
    0x6C31,  0x6C4C,  0x6C66,  0x6C81,
    0x6C9C,  0x6CB6,  0x6CD1,  0x6CEB,
    0x6D06,  0x6D20,  0x6D3A,  0x6D54,
    0x6D6E,  0x6D88,  0x6DA2,  0x6DBC,
    0x6DD6,  0x6DF0,  0x6E0A,  0x6E23,
    0x6E3D,  0x6E56,  0x6E70,  0x6E89,
    0x6EA2,  0x6EBC,  0x6ED5,  0x6EEE,
    0x6F07,  0x6F20,  0x6F39,  0x6F52,
    0x6F6B,  0x6F83,  0x6F9C,  0x6FB4,
    0x6FCD,  0x6FE5,  0x6FFE,  0x7016,
    0x702E,  0x7046,  0x705F,  0x7077,
    0x708F,  0x70A6,  0x70BE,  0x70D6,
    0x70EE,  0x7105,  0x711D,  0x7134,
    0x714C,  0x7163,  0x717A,  0x7192,
    0x71A9,  0x71C0,  0x71D7,  0x71EE,
    0x7205,  0x721C,  0x7232,  0x7249,
    0x7260,  0x7276,  0x728D,  0x72A3,
    0x72B9,  0x72D0,  0x72E6,  0x72FC,
    0x7312,  0x7328,  0x733E,  0x7354,
    0x7369,  0x737F,  0x7395,  0x73AA,
    0x73C0,  0x73D5,  0x73EB,  0x7400,
    0x7415,  0x742A,  0x743F,  0x7454,
    0x7469,  0x747E,  0x7493,  0x74A8,
    0x74BC,  0x74D1,  0x74E5,  0x74FA,
    0x750E,  0x7522,  0x7537,  0x754B,
    0x755F,  0x7573,  0x7587,  0x759B,
    0x75AE,  0x75C2,  0x75D6,  0x75E9,
    0x75FD,  0x7610,  0x7624,  0x7637,
    0x764A,  0x765E,  0x7671,  0x7684,
    0x7697,  0x76A9,  0x76BC,  0x76CF,
    0x76E2,  0x76F4,  0x7707,  0x7719,
    0x772C,  0x773E,  0x7750,  0x7762,
    0x7774,  0x7786,  0x7798,  0x77AA,
    0x77BC,  0x77CE,  0x77DF,  0x77F1,
    0x7803,  0x7814,  0x7825,  0x7837,
    0x7848,  0x7859,  0x786A,  0x787B,
    0x788C,  0x789D,  0x78AE,  0x78BE,
    0x78CF,  0x78E0,  0x78F0,  0x7901,
    0x7911,  0x7921,  0x7931,  0x7941,
    0x7952,  0x7962,  0x7971,  0x7981,
    0x7991,  0x79A1,  0x79B0,  0x79C0,
    0x79CF,  0x79DF,  0x79EE,  0x79FD,
    0x7A0D,  0x7A1C,  0x7A2B,  0x7A3A,
    0x7A49,  0x7A57,  0x7A66,  0x7A75,
    0x7A83,  0x7A92,  0x7AA0,  0x7AAF,
    0x7ABD,  0x7ACB,  0x7AD9,  0x7AE7,
    0x7AF5,  0x7B03,  0x7B11,  0x7B1F,
    0x7B2D,  0x7B3A,  0x7B48,  0x7B55,
    0x7B63,  0x7B70,  0x7B7D,  0x7B8B,
    0x7B98,  0x7BA5,  0x7BB2,  0x7BBF,
    0x7BCB,  0x7BD8,  0x7BE5,  0x7BF1,
    0x7BFE,  0x7C0A,  0x7C17,  0x7C23,
    0x7C2F,  0x7C3B,  0x7C47,  0x7C53,
    0x7C5F,  0x7C6B,  0x7C77,  0x7C83,
    0x7C8E,  0x7C9A,  0x7CA5,  0x7CB1,
    0x7CBC,  0x7CC7,  0x7CD2,  0x7CDD,
    0x7CE8,  0x7CF3,  0x7CFE,  0x7D09,
    0x7D14,  0x7D1E,  0x7D29,  0x7D33,
    0x7D3E,  0x7D48,  0x7D52,  0x7D5C,
    0x7D67,  0x7D71,  0x7D7B,  0x7D84,
    0x7D8E,  0x7D98,  0x7DA2,  0x7DAB,
    0x7DB5,  0x7DBE,  0x7DC8,  0x7DD1,
    0x7DDA,  0x7DE3,  0x7DEC,  0x7DF5,
    0x7DFE,  0x7E07,  0x7E10,  0x7E18,
    0x7E21,  0x7E29,  0x7E32,  0x7E3A,
    0x7E42,  0x7E4B,  0x7E53,  0x7E5B,
    0x7E63,  0x7E6B,  0x7E73,  0x7E7A,
    0x7E82,  0x7E8A,  0x7E91,  0x7E99,
    0x7EA0,  0x7EA7,  0x7EAF,  0x7EB6,
    0x7EBD,  0x7EC4,  0x7ECB,  0x7ED2,
    0x7ED8,  0x7EDF,  0x7EE6,  0x7EEC,
    0x7EF3,  0x7EF9,  0x7EFF,  0x7F05,
    0x7F0C,  0x7F12,  0x7F18,  0x7F1E,
    0x7F23,  0x7F29,  0x7F2F,  0x7F35,
    0x7F3A,  0x7F40,  0x7F45,  0x7F4A,
    0x7F50,  0x7F55,  0x7F5A,  0x7F5F,
    0x7F64,  0x7F69,  0x7F6D,  0x7F72,
    0x7F77,  0x7F7B,  0x7F80,  0x7F84,
    0x7F88,  0x7F8D,  0x7F91,  0x7F95,
    0x7F99,  0x7F9D,  0x7FA1,  0x7FA4,
    0x7FA8,  0x7FAC,  0x7FAF,  0x7FB3,
    0x7FB6,  0x7FB9,  0x7FBD,  0x7FC0,
    0x7FC3,  0x7FC6,  0x7FC9,  0x7FCC,
    0x7FCE,  0x7FD1,  0x7FD4,  0x7FD6,
    0x7FD9,  0x7FDB,  0x7FDD,  0x7FE0,
    0x7FE2,  0x7FE4,  0x7FE6,  0x7FE8,
    0x7FEA,  0x7FEB,  0x7FED,  0x7FEF,
    0x7FF0,  0x7FF2,  0x7FF3,  0x7FF5,
    0x7FF6,  0x7FF7,  0x7FF8,  0x7FF9,
    0x7FFA,  0x7FFB,  0x7FFC,  0x7FFC,
    0x7FFD,  0x7FFD,  0x7FFE,  0x7FFE,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF

};


const Int16 Short_Window_sine_fxp[SHORT_WINDOW] =
{

    0x00C9,  0x025B,  0x03ED,  0x057F,
    0x0711,  0x08A2,  0x0A33,  0x0BC4,
    0x0D54,  0x0EE3,  0x1072,  0x1201,
    0x138F,  0x151C,  0x16A8,  0x1833,
    0x19BE,  0x1B47,  0x1CCF,  0x1E57,
    0x1FDD,  0x2161,  0x22E5,  0x2467,
    0x25E8,  0x2767,  0x28E5,  0x2A61,
    0x2BDC,  0x2D55,  0x2ECC,  0x3041,
    0x31B5,  0x3326,  0x3496,  0x3604,
    0x376F,  0x38D9,  0x3A40,  0x3BA5,
    0x3D07,  0x3E68,  0x3FC5,  0x4121,
    0x427A,  0x43D0,  0x4524,  0x4675,
    0x47C3,  0x490F,  0x4A58,  0x4B9D,
    0x4CE0,  0x4E20,  0x4F5D,  0x5097,
    0x51CE,  0x5302,  0x5432,  0x5560,
    0x568A,  0x57B0,  0x58D3,  0x59F3,
    0x5B0F,  0x5C28,  0x5D3E,  0x5E4F,
    0x5F5D,  0x6068,  0x616E,  0x6271,
    0x6370,  0x646C,  0x6563,  0x6656,
    0x6746,  0x6832,  0x6919,  0x69FD,
    0x6ADC,  0x6BB7,  0x6C8E,  0x6D61,
    0x6E30,  0x6EFB,  0x6FC1,  0x7083,
    0x7140,  0x71F9,  0x72AE,  0x735E,
    0x740A,  0x74B2,  0x7555,  0x75F3,
    0x768D,  0x7722,  0x77B3,  0x783F,
    0x78C7,  0x794A,  0x79C8,  0x7A41,
    0x7AB6,  0x7B26,  0x7B91,  0x7BF8,
    0x7C59,  0x7CB6,  0x7D0E,  0x7D62,
    0x7DB0,  0x7DFA,  0x7E3E,  0x7E7E,
    0x7EB9,  0x7EEF,  0x7F21,  0x7F4D,
    0x7F74,  0x7F97,  0x7FB4,  0x7FCD,
    0x7FE1,  0x7FF0,  0x7FF9,  0x7FFE
};



const Int16 Long_Window_KBD_fxp[LONG_WINDOW] =
{

    0x000A,  0x000E,  0x0012,  0x0015,
    0x0019,  0x001C,  0x0020,  0x0023,
    0x0026,  0x002A,  0x002D,  0x0030,
    0x0034,  0x0038,  0x003B,  0x003F,
    0x0043,  0x0047,  0x004B,  0x004F,
    0x0053,  0x0057,  0x005B,  0x0060,
    0x0064,  0x0069,  0x006D,  0x0072,
    0x0077,  0x007C,  0x0081,  0x0086,
    0x008B,  0x0091,  0x0096,  0x009C,
    0x00A1,  0x00A7,  0x00AD,  0x00B3,
    0x00B9,  0x00BF,  0x00C6,  0x00CC,
    0x00D3,  0x00DA,  0x00E0,  0x00E7,
    0x00EE,  0x00F5,  0x00FD,  0x0104,
    0x010C,  0x0113,  0x011B,  0x0123,
    0x012B,  0x0133,  0x013C,  0x0144,
    0x014D,  0x0156,  0x015F,  0x0168,
    0x0171,  0x017A,  0x0183,  0x018D,
    0x0197,  0x01A1,  0x01AB,  0x01B5,
    0x01BF,  0x01CA,  0x01D4,  0x01DF,
    0x01EA,  0x01F5,  0x0200,  0x020C,
    0x0217,  0x0223,  0x022F,  0x023B,
    0x0247,  0x0253,  0x0260,  0x026D,
    0x027A,  0x0287,  0x0294,  0x02A1,
    0x02AF,  0x02BC,  0x02CA,  0x02D8,
    0x02E7,  0x02F5,  0x0304,  0x0312,
    0x0321,  0x0331,  0x0340,  0x034F,
    0x035F,  0x036F,  0x037F,  0x038F,
    0x03A0,  0x03B0,  0x03C1,  0x03D2,
    0x03E3,  0x03F5,  0x0406,  0x0418,
    0x042A,  0x043C,  0x044F,  0x0461,
    0x0474,  0x0487,  0x049A,  0x04AE,
    0x04C1,  0x04D5,  0x04E9,  0x04FD,
    0x0512,  0x0526,  0x053B,  0x0550,
    0x0566,  0x057B,  0x0591,  0x05A7,
    0x05BD,  0x05D3,  0x05EA,  0x0601,
    0x0618,  0x062F,  0x0646,  0x065E,
    0x0676,  0x068E,  0x06A6,  0x06BF,
    0x06D8,  0x06F1,  0x070A,  0x0723,
    0x073D,  0x0757,  0x0771,  0x078C,
    0x07A6,  0x07C1,  0x07DC,  0x07F7,
    0x0813,  0x082F,  0x084B,  0x0867,
    0x0884,  0x08A0,  0x08BD,  0x08DA,
    0x08F8,  0x0916,  0x0933,  0x0952,
    0x0970,  0x098F,  0x09AE,  0x09CD,
    0x09EC,  0x0A0C,  0x0A2C,  0x0A4C,
    0x0A6C,  0x0A8D,  0x0AAD,  0x0ACF,
    0x0AF0,  0x0B11,  0x0B33,  0x0B55,
    0x0B78,  0x0B9A,  0x0BBD,  0x0BE0,
    0x0C03,  0x0C27,  0x0C4B,  0x0C6F,
    0x0C93,  0x0CB8,  0x0CDD,  0x0D02,
    0x0D27,  0x0D4D,  0x0D73,  0x0D99,
    0x0DBF,  0x0DE6,  0x0E0C,  0x0E33,
    0x0E5B,  0x0E82,  0x0EAA,  0x0ED2,
    0x0EFB,  0x0F23,  0x0F4C,  0x0F75,
    0x0F9F,  0x0FC8,  0x0FF2,  0x101C,
    0x1047,  0x1071,  0x109C,  0x10C7,
    0x10F3,  0x111E,  0x114A,  0x1176,
    0x11A3,  0x11D0,  0x11FC,  0x122A,
    0x1257,  0x1285,  0x12B3,  0x12E1,
    0x130F,  0x133E,  0x136D,  0x139C,
    0x13CB,  0x13FB,  0x142B,  0x145B,
    0x148B,  0x14BC,  0x14ED,  0x151E,
    0x1550,  0x1581,  0x15B3,  0x15E5,
    0x1618,  0x164A,  0x167D,  0x16B0,
    0x16E3,  0x1717,  0x174B,  0x177F,
    0x17B3,  0x17E8,  0x181D,  0x1852,
    0x1887,  0x18BC,  0x18F2,  0x1928,
    0x195E,  0x1995,  0x19CB,  0x1A02,
    0x1A39,  0x1A71,  0x1AA8,  0x1AE0,
    0x1B18,  0x1B50,  0x1B89,  0x1BC1,
    0x1BFA,  0x1C34,  0x1C6D,  0x1CA7,
    0x1CE0,  0x1D1A,  0x1D55,  0x1D8F,
    0x1DCA,  0x1E05,  0x1E40,  0x1E7B,
    0x1EB7,  0x1EF2,  0x1F2E,  0x1F6B,
    0x1FA7,  0x1FE4,  0x2020,  0x205D,
    0x209B,  0x20D8,  0x2116,  0x2153,
    0x2191,  0x21D0,  0x220E,  0x224D,
    0x228B,  0x22CA,  0x2309,  0x2349,
    0x2388,  0x23C8,  0x2408,  0x2448,
    0x2488,  0x24C9,  0x2509,  0x254A,
    0x258B,  0x25CC,  0x260E,  0x264F,
    0x2691,  0x26D3,  0x2715,  0x2757,
    0x2799,  0x27DC,  0x281F,  0x2861,
    0x28A4,  0x28E8,  0x292B,  0x296E,
    0x29B2,  0x29F6,  0x2A3A,  0x2A7E,
    0x2AC2,  0x2B06,  0x2B4B,  0x2B8F,
    0x2BD4,  0x2C19,  0x2C5E,  0x2CA3,
    0x2CE9,  0x2D2E,  0x2D74,  0x2DB9,
    0x2DFF,  0x2E45,  0x2E8B,  0x2ED1,
    0x2F18,  0x2F5E,  0x2FA5,  0x2FEB,
    0x3032,  0x3079,  0x30C0,  0x3107,
    0x314E,  0x3195,  0x31DD,  0x3224,
    0x326C,  0x32B4,  0x32FB,  0x3343,
    0x338B,  0x33D3,  0x341B,  0x3463,
    0x34AC,  0x34F4,  0x353D,  0x3585,
    0x35CE,  0x3616,  0x365F,  0x36A8,
    0x36F1,  0x373A,  0x3783,  0x37CC,
    0x3815,  0x385E,  0x38A7,  0x38F0,
    0x393A,  0x3983,  0x39CC,  0x3A16,
    0x3A5F,  0x3AA9,  0x3AF2,  0x3B3C,
    0x3B86,  0x3BCF,  0x3C19,  0x3C63,
    0x3CAC,  0x3CF6,  0x3D40,  0x3D8A,
    0x3DD3,  0x3E1D,  0x3E67,  0x3EB1,
    0x3EFB,  0x3F45,  0x3F8E,  0x3FD8,
    0x4022,  0x406C,  0x40B6,  0x4100,
    0x414A,  0x4193,  0x41DD,  0x4227,
    0x4271,  0x42BB,  0x4304,  0x434E,
    0x4398,  0x43E1,  0x442B,  0x4475,
    0x44BE,  0x4508,  0x4551,  0x459B,
    0x45E4,  0x462E,  0x4677,  0x46C0,
    0x4709,  0x4753,  0x479C,  0x47E5,
    0x482E,  0x4877,  0x48C0,  0x4909,
    0x4951,  0x499A,  0x49E3,  0x4A2B,
    0x4A74,  0x4ABC,  0x4B04,  0x4B4D,
    0x4B95,  0x4BDD,  0x4C25,  0x4C6D,
    0x4CB5,  0x4CFC,  0x4D44,  0x4D8C,
    0x4DD3,  0x4E1A,  0x4E62,  0x4EA9,
    0x4EF0,  0x4F37,  0x4F7E,  0x4FC4,
    0x500B,  0x5051,  0x5098,  0x50DE,
    0x5124,  0x516A,  0x51B0,  0x51F6,
    0x523B,  0x5281,  0x52C6,  0x530B,
    0x5351,  0x5396,  0x53DA,  0x541F,
    0x5464,  0x54A8,  0x54EC,  0x5530,
    0x5574,  0x55B8,  0x55FC,  0x563F,
    0x5683,  0x56C6,  0x5709,  0x574C,
    0x578F,  0x57D1,  0x5814,  0x5856,
    0x5898,  0x58DA,  0x591B,  0x595D,
    0x599E,  0x59E0,  0x5A21,  0x5A61,
    0x5AA2,  0x5AE3,  0x5B23,  0x5B63,
    0x5BA3,  0x5BE3,  0x5C22,  0x5C62,
    0x5CA1,  0x5CE0,  0x5D1F,  0x5D5D,
    0x5D9C,  0x5DDA,  0x5E18,  0x5E56,
    0x5E93,  0x5ED1,  0x5F0E,  0x5F4B,
    0x5F87,  0x5FC4,  0x6000,  0x603D,
    0x6079,  0x60B4,  0x60F0,  0x612B,
    0x6166,  0x61A1,  0x61DC,  0x6216,
    0x6250,  0x628A,  0x62C4,  0x62FE,
    0x6337,  0x6370,  0x63A9,  0x63E2,
    0x641A,  0x6452,  0x648A,  0x64C2,
    0x64F9,  0x6531,  0x6568,  0x659E,
    0x65D5,  0x660B,  0x6641,  0x6677,
    0x66AD,  0x66E2,  0x6717,  0x674C,
    0x6781,  0x67B5,  0x67E9,  0x681D,
    0x6851,  0x6885,  0x68B8,  0x68EB,
    0x691D,  0x6950,  0x6982,  0x69B4,
    0x69E6,  0x6A17,  0x6A48,  0x6A79,
    0x6AAA,  0x6ADB,  0x6B0B,  0x6B3B,
    0x6B6A,  0x6B9A,  0x6BC9,  0x6BF8,
    0x6C27,  0x6C55,  0x6C83,  0x6CB1,
    0x6CDF,  0x6D0D,  0x6D3A,  0x6D67,
    0x6D93,  0x6DC0,  0x6DEC,  0x6E18,
    0x6E44,  0x6E6F,  0x6E9A,  0x6EC5,
    0x6EF0,  0x6F1A,  0x6F44,  0x6F6E,
    0x6F98,  0x6FC1,  0x6FEA,  0x7013,
    0x703C,  0x7064,  0x708C,  0x70B4,
    0x70DB,  0x7103,  0x712A,  0x7151,
    0x7177,  0x719D,  0x71C3,  0x71E9,
    0x720F,  0x7234,  0x7259,  0x727E,
    0x72A2,  0x72C7,  0x72EB,  0x730E,
    0x7332,  0x7355,  0x7378,  0x739B,
    0x73BD,  0x73E0,  0x7402,  0x7424,
    0x7445,  0x7466,  0x7487,  0x74A8,
    0x74C9,  0x74E9,  0x7509,  0x7529,
    0x7548,  0x7568,  0x7587,  0x75A5,
    0x75C4,  0x75E2,  0x7601,  0x761E,
    0x763C,  0x7659,  0x7676,  0x7693,
    0x76B0,  0x76CC,  0x76E9,  0x7705,
    0x7720,  0x773C,  0x7757,  0x7772,
    0x778D,  0x77A8,  0x77C2,  0x77DC,
    0x77F6,  0x780F,  0x7829,  0x7842,
    0x785B,  0x7874,  0x788C,  0x78A5,
    0x78BD,  0x78D5,  0x78EC,  0x7904,
    0x791B,  0x7932,  0x7949,  0x795F,
    0x7976,  0x798C,  0x79A2,  0x79B7,
    0x79CD,  0x79E2,  0x79F7,  0x7A0C,
    0x7A21,  0x7A35,  0x7A4A,  0x7A5E,
    0x7A72,  0x7A85,  0x7A99,  0x7AAC,
    0x7ABF,  0x7AD2,  0x7AE5,  0x7AF7,
    0x7B09,  0x7B1B,  0x7B2D,  0x7B3F,
    0x7B51,  0x7B62,  0x7B73,  0x7B84,
    0x7B95,  0x7BA5,  0x7BB6,  0x7BC6,
    0x7BD6,  0x7BE6,  0x7BF6,  0x7C05,
    0x7C15,  0x7C24,  0x7C33,  0x7C42,
    0x7C50,  0x7C5F,  0x7C6D,  0x7C7B,
    0x7C89,  0x7C97,  0x7CA5,  0x7CB2,
    0x7CC0,  0x7CCD,  0x7CDA,  0x7CE7,
    0x7CF3,  0x7D00,  0x7D0C,  0x7D18,
    0x7D25,  0x7D31,  0x7D3C,  0x7D48,
    0x7D53,  0x7D5F,  0x7D6A,  0x7D75,
    0x7D80,  0x7D8B,  0x7D95,  0x7DA0,
    0x7DAA,  0x7DB4,  0x7DBE,  0x7DC8,
    0x7DD2,  0x7DDC,  0x7DE5,  0x7DEF,
    0x7DF8,  0x7E01,  0x7E0A,  0x7E13,
    0x7E1C,  0x7E25,  0x7E2D,  0x7E36,
    0x7E3E,  0x7E46,  0x7E4E,  0x7E56,
    0x7E5E,  0x7E66,  0x7E6D,  0x7E75,
    0x7E7C,  0x7E83,  0x7E8B,  0x7E92,
    0x7E99,  0x7EA0,  0x7EA6,  0x7EAD,
    0x7EB3,  0x7EBA,  0x7EC0,  0x7EC6,
    0x7ECD,  0x7ED3,  0x7ED9,  0x7EDE,
    0x7EE4,  0x7EEA,  0x7EF0,  0x7EF5,
    0x7EFA,  0x7F00,  0x7F05,  0x7F0A,
    0x7F0F,  0x7F14,  0x7F19,  0x7F1E,
    0x7F23,  0x7F27,  0x7F2C,  0x7F30,
    0x7F35,  0x7F39,  0x7F3D,  0x7F41,
    0x7F46,  0x7F4A,  0x7F4E,  0x7F52,
    0x7F55,  0x7F59,  0x7F5D,  0x7F60,
    0x7F64,  0x7F68,  0x7F6B,  0x7F6E,
    0x7F72,  0x7F75,  0x7F78,  0x7F7B,
    0x7F7E,  0x7F81,  0x7F84,  0x7F87,
    0x7F8A,  0x7F8D,  0x7F90,  0x7F92,
    0x7F95,  0x7F97,  0x7F9A,  0x7F9C,
    0x7F9F,  0x7FA1,  0x7FA4,  0x7FA6,
    0x7FA8,  0x7FAA,  0x7FAC,  0x7FAE,
    0x7FB1,  0x7FB3,  0x7FB5,  0x7FB6,
    0x7FB8,  0x7FBA,  0x7FBC,  0x7FBE,
    0x7FBF,  0x7FC1,  0x7FC3,  0x7FC4,
    0x7FC6,  0x7FC8,  0x7FC9,  0x7FCB,
    0x7FCC,  0x7FCD,  0x7FCF,  0x7FD0,
    0x7FD1,  0x7FD3,  0x7FD4,  0x7FD5,
    0x7FD6,  0x7FD8,  0x7FD9,  0x7FDA,
    0x7FDB,  0x7FDC,  0x7FDD,  0x7FDE,
    0x7FDF,  0x7FE0,  0x7FE1,  0x7FE2,
    0x7FE3,  0x7FE4,  0x7FE4,  0x7FE5,
    0x7FE6,  0x7FE7,  0x7FE8,  0x7FE8,
    0x7FE9,  0x7FEA,  0x7FEA,  0x7FEB,
    0x7FEC,  0x7FEC,  0x7FED,  0x7FEE,
    0x7FEE,  0x7FEF,  0x7FEF,  0x7FF0,
    0x7FF0,  0x7FF1,  0x7FF1,  0x7FF2,
    0x7FF2,  0x7FF3,  0x7FF3,  0x7FF4,
    0x7FF4,  0x7FF4,  0x7FF5,  0x7FF5,
    0x7FF6,  0x7FF6,  0x7FF6,  0x7FF7,
    0x7FF7,  0x7FF7,  0x7FF8,  0x7FF8,
    0x7FF8,  0x7FF8,  0x7FF9,  0x7FF9,
    0x7FF9,  0x7FF9,  0x7FFA,  0x7FFA,
    0x7FFA,  0x7FFA,  0x7FFA,  0x7FFB,
    0x7FFB,  0x7FFB,  0x7FFB,  0x7FFB,
    0x7FFC,  0x7FFC,  0x7FFC,  0x7FFC,
    0x7FFC,  0x7FFC,  0x7FFC,  0x7FFC,
    0x7FFD,  0x7FFD,  0x7FFD,  0x7FFD,
    0x7FFD,  0x7FFD,  0x7FFD,  0x7FFD,
    0x7FFD,  0x7FFD,  0x7FFE,  0x7FFE,
    0x7FFE,  0x7FFE,  0x7FFE,  0x7FFE,
    0x7FFE,  0x7FFE,  0x7FFE,  0x7FFE,
    0x7FFE,  0x7FFE,  0x7FFE,  0x7FFE,
    0x7FFE,  0x7FFE,  0x7FFE,  0x7FFE,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF

};




const Int16 Short_Window_KBD_fxp[SHORT_WINDOW] =
{

    0x0001,  0x0004,  0x0008,  0x000D,
    0x0014,  0x001D,  0x0029,  0x0039,
    0x004C,  0x0063,  0x0080,  0x00A2,
    0x00CB,  0x00FB,  0x0133,  0x0174,
    0x01BE,  0x0214,  0x0275,  0x02E3,
    0x035E,  0x03E8,  0x0481,  0x052B,
    0x05E7,  0x06B4,  0x0795,  0x088A,
    0x0993,  0x0AB2,  0x0BE7,  0x0D32,
    0x0E94,  0x100E,  0x119F,  0x1347,
    0x1507,  0x16DE,  0x18CC,  0x1AD0,
    0x1CEB,  0x1F1A,  0x215F,  0x23B6,
    0x2620,  0x289C,  0x2B27,  0x2DC0,
    0x3066,  0x3317,  0x35D2,  0x3894,
    0x3B5C,  0x3E28,  0x40F6,  0x43C4,
    0x468F,  0x4956,  0x4C18,  0x4ED1,
    0x5181,  0x5425,  0x56BC,  0x5944,
    0x5BBB,  0x5E21,  0x6073,  0x62B1,
    0x64DA,  0x66EC,  0x68E7,  0x6ACB,
    0x6C96,  0x6E49,  0x6FE4,  0x7166,
    0x72D0,  0x7421,  0x755B,  0x767E,
    0x778A,  0x7881,  0x7962,  0x7A30,
    0x7AEA,  0x7B92,  0x7C29,  0x7CB0,
    0x7D28,  0x7D92,  0x7DF0,  0x7E42,
    0x7E89,  0x7EC7,  0x7EFC,  0x7F2A,
    0x7F50,  0x7F71,  0x7F8C,  0x7FA3,
    0x7FB6,  0x7FC5,  0x7FD2,  0x7FDC,
    0x7FE4,  0x7FEB,  0x7FF0,  0x7FF4,
    0x7FF7,  0x7FF9,  0x7FFB,  0x7FFC,
    0x7FFD,  0x7FFE,  0x7FFE,  0x7FFE,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF,
    0x7FFF,  0x7FFF,  0x7FFF,  0x7FFF
};

