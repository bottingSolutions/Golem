/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solutions.botting.golem.api;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import static org.opencv.core.CvType.CV_32FC1;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.MORPH_CLOSE;
import static org.opencv.imgproc.Imgproc.MORPH_ELLIPSE;
import static org.opencv.imgproc.Imgproc.MORPH_GRADIENT;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.THRESH_OTSU;
import static org.opencv.imgproc.Imgproc.TM_CCOEFF_NORMED;
import static org.opencv.imgproc.Imgproc.TM_CCORR_NORMED;
import static org.opencv.imgproc.Imgproc.TM_SQDIFF;
import static org.opencv.imgproc.Imgproc.TM_SQDIFF_NORMED;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.morphologyEx;
import static org.opencv.imgproc.Imgproc.threshold;
import solutions.botting.golem.Golem;
import solutions.botting.golem.shape.ShapeBounds;
import solutions.botting.golem.shape.ShapeType;

/**
 *
 * @author unsignedByte <admin@botting.solutions>
 */
public class OpenCVApi extends GolemApi {

    public OpenCVApi(Golem golem) {
        super(golem);
    }

    public List<ShapeBounds> getShapes(Mat image, Mat source) throws IOException {
        ArrayList<ShapeBounds> shapes = new ArrayList<>();

        Mat hierarchy = new Mat();

        MatOfPoint2f approxCurve = new MatOfPoint2f();
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(
                image,
                contours,
                hierarchy,
                Imgproc.RETR_LIST,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

        // loop over all found contours
        for (MatOfPoint cnt : contours) {
            MatOfPoint2f curve = new MatOfPoint2f(cnt.toArray());

            // approximates a polygonal curve with the specified precision
            Imgproc.approxPolyDP(
                    curve,
                    approxCurve,
                    0.02 * Imgproc.arcLength(curve, true),
                    false
            );

            int numberVertices = (int) approxCurve.total();
            double contourArea = Imgproc.contourArea(cnt);

            // ignore to small areas
            if (Math.abs(contourArea) < 0 // || !Imgproc.isContourConvex(
                    ) {
                continue;
            }

            // triangle detection
            if (numberVertices == 3) {
                shapes.add(new ShapeBounds(ShapeType.TRIANGLE, this.getBoundingRectangleWithinBotBounds(cnt)));
            }

            // rectangle, pentagon and hexagon detection
            if (numberVertices > 6) {
                shapes.add(new ShapeBounds(ShapeType.POLYGON, this.getBoundingRectangleWithinBotBounds(cnt)));
            }
            if (numberVertices >= 4 && numberVertices <= 6) {
                List<Double> cos = new ArrayList<>();
                for (int j = 2; j < numberVertices + 1; j++) {
                    cos.add(
                            angle(
                                    approxCurve.toArray()[j % numberVertices],
                                    approxCurve.toArray()[j - 2],
                                    approxCurve.toArray()[j - 1]
                            )
                    );
                }
                Collections.sort(cos);

                double mincos = cos.get(0);
                double maxcos = cos.get(cos.size() - 1);

                // rectangle detection
                if (numberVertices == 4
                        && mincos >= -0.1 && maxcos <= 0.3) {
                    Rect r = Imgproc.boundingRect(cnt);
                    shapes.add(new ShapeBounds(ShapeType.RECTANGLE, this.getBoundingRectangleWithinBotBounds(cnt)));
                    Imgproc.rectangle(source, r.tl(), r.br(), new Scalar(255, 128, 128));

                } // pentagon detection
                else if (numberVertices == 5
                        && mincos >= -0.34 && maxcos <= -0.27) {
                    shapes.add(new ShapeBounds(ShapeType.PENTAGON, this.getBoundingRectangleWithinBotBounds(cnt)));

                } // hexagon detection
                else if (numberVertices == 6
                        && mincos >= -0.55 && maxcos <= -0.45) {
                    shapes.add(new ShapeBounds(ShapeType.HEXAGON, this.getBoundingRectangleWithinBotBounds(cnt)));

                }
            } // circle detection
            else {
                Rect r = Imgproc.boundingRect(cnt);

                int radius = r.width / 2;

                if (Math.abs(
                        1 - (r.width / r.height)
                ) <= 0.2
                        && Math.abs(
                                1 - (contourArea / (Math.PI * radius * radius))
                        ) <= 0.2) {

                    shapes.add(new ShapeBounds(ShapeType.CIRCLE, this.getBoundingRectangleWithinBotBounds(cnt)));
                }

            }

        }

        return shapes;

    }

    /**
     * Helper function to find a cosine of angle between vectors from pt0->pt1
     * and pt0->pt2
     */
    private static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2)
                / Math.sqrt(
                        (dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10
                );
    }

    public Rectangle matchTemplate(Mat source, Mat template, double accuracy) {

        int result_cols = source.cols() - template.cols() + 1;
        int result_rows = source.rows() - template.rows() + 1;
        if (source.channels() > 2) {
            Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);
        }
        if (template.channels() > 2) {
            Imgproc.cvtColor(template, template, Imgproc.COLOR_BGR2GRAY);
        }
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
        int match_method = TM_CCOEFF_NORMED;

        Imgproc.matchTemplate(source, template, result, match_method);

        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        if (mmr.maxVal < accuracy) {
            return null;
        }

        Point matchLoc = null;
        //  For all the other methods, the higher the better
        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
        } else {
            matchLoc = mmr.maxLoc;
        }
        Double x = matchLoc.x;
        Double y = matchLoc.y;
        return new Rectangle(x.intValue(), y.intValue(), template.cols(), template.rows());
    }

    public Rectangle matchTemplate(Mat source, Mat template) {
        return matchTemplate(source, template, 91);
    }

    public Mat morphEx(Mat source, int size) {
        Mat grad = new Mat();
        Mat morphKernel = getStructuringElement(MORPH_ELLIPSE, new Size(size, size));
        morphologyEx(source, grad, MORPH_GRADIENT, morphKernel);
        return grad;
    }

}
